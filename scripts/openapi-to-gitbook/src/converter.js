'use strict';

const fs = require('fs');
const path = require('path');
const crypto = require('crypto');
const { getDir } = require('./domainMapper');
const { generateEndpointMarkdown, AUTO_MARKER, resolveRef } = require('./markdownGen');
const { scanAndMergeSummary } = require('./summaryGen');

const SNAPSHOT_FILE = '.openapi-snapshot.json';
const METHODS = ['get', 'post', 'put', 'patch', 'delete'];

function collectRefs(obj, spec, depth, refs) {
  if (!obj || typeof obj !== 'object' || depth > 3) return refs;

  if (obj.$ref && !refs[obj.$ref]) {
    const resolved = resolveRef(obj.$ref, spec);
    if (resolved) {
      refs[obj.$ref] = resolved;
      collectRefs(resolved, spec, depth + 1, refs);
    }
  }

  if (Array.isArray(obj)) {
    for (const item of obj) collectRefs(item, spec, depth, refs);
  } else {
    for (const val of Object.values(obj)) collectRefs(val, spec, depth, refs);
  }

  return refs;
}

function computeHash(operation, spec) {
  const refs = collectRefs(operation, spec, 0, {});
  const input = JSON.stringify({ operation, refs });
  return crypto.createHash('md5').update(input).digest('hex');
}

function generateFileName(method, pathStr) {
  const segments = pathStr
    .replace(/^\/api\//, '')
    .replace(/^\//, '')
    .split('/')
    .map(s => s.replace(/[{}]/g, ''))
    .filter(Boolean);
  return `${method}-${segments.join('-')}.md`;
}

function convert(spec, outputDir) {
  const snapshotPath = path.join(outputDir, SNAPSHOT_FILE);

  // Load previous snapshot (endpoint hash map)
  let prevHashes = null;
  if (fs.existsSync(snapshotPath)) {
    try {
      prevHashes = JSON.parse(fs.readFileSync(snapshotPath, 'utf-8'));
    } catch {
      prevHashes = null;
    }
  }

  // Build current hash map
  const currentHashes = {};
  for (const [pathStr, pathItem] of Object.entries(spec.paths || {})) {
    for (const method of METHODS) {
      if (!pathItem[method]) continue;
      const key = `${method.toUpperCase()} ${pathStr}`;
      currentHashes[key] = computeHash(pathItem[method], spec);
    }
  }

  // First run: save baseline, generate nothing
  if (!prevHashes) {
    console.log('No previous snapshot found. Saving baseline snapshot.');
    fs.mkdirSync(outputDir, { recursive: true });
    fs.writeFileSync(snapshotPath, JSON.stringify(currentHashes, null, 2), 'utf-8');
    return { filesWritten: 0, skipped: 0, domains: 0 };
  }

  // Find new/changed endpoints
  let filesWritten = 0;
  let skipped = 0;
  const domainSet = new Set();

  for (const [pathStr, pathItem] of Object.entries(spec.paths || {})) {
    for (const method of METHODS) {
      const operation = pathItem[method];
      if (!operation) continue;

      const key = `${method.toUpperCase()} ${pathStr}`;

      // Skip unchanged endpoints
      if (prevHashes[key] === currentHashes[key]) continue;

      const tag = operation.tags?.[0] || '기타';
      const dir = getDir(tag);
      domainSet.add(dir);

      const fileName = generateFileName(method, pathStr);
      const filePath = path.join(outputDir, dir, fileName);

      // Skip manual files (no auto marker)
      if (fs.existsSync(filePath)) {
        const existing = fs.readFileSync(filePath, 'utf-8');
        if (!existing.startsWith(AUTO_MARKER)) {
          skipped++;
          continue;
        }
      }

      const markdown = generateEndpointMarkdown(method, pathStr, operation, spec);

      fs.mkdirSync(path.dirname(filePath), { recursive: true });
      fs.writeFileSync(filePath, markdown, 'utf-8');
      filesWritten++;
    }
  }

  // Save updated snapshot
  fs.writeFileSync(snapshotPath, JSON.stringify(currentHashes, null, 2), 'utf-8');

  // Scan all auto-generated files and merge SUMMARY.md
  scanAndMergeSummary(outputDir);

  return { filesWritten, skipped, domains: domainSet.size };
}

module.exports = { convert };
