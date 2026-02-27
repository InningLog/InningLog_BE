'use strict';

const fs = require('fs');
const path = require('path');
const { getDir } = require('./domainMapper');
const { generateEndpointMarkdown, AUTO_MARKER } = require('./markdownGen');
const { mergeSummary } = require('./summaryGen');

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
  const endpoints = [];
  let filesWritten = 0;
  let skipped = 0;
  const domainSet = new Set();

  const methods = ['get', 'post', 'put', 'patch', 'delete'];

  for (const [pathStr, pathItem] of Object.entries(spec.paths || {})) {
    for (const method of methods) {
      const operation = pathItem[method];
      if (!operation) continue;

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

      endpoints.push({
        tag,
        dir,
        fileName,
        title: operation.summary || `${method.toUpperCase()} ${pathStr}`,
      });
    }
  }

  mergeSummary(outputDir, endpoints);

  return { filesWritten, skipped, domains: domainSet.size };
}

module.exports = { convert };
