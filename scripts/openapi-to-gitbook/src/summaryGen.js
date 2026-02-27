'use strict';

const fs = require('fs');
const path = require('path');
const { getTagLabel } = require('./domainMapper');

const START_MARKER = '<!-- AUTO-GENERATED-START -->';
const END_MARKER = '<!-- AUTO-GENERATED-END -->';

function buildAutoSection(endpoints) {
  // Group by domain directory
  const groups = {};
  for (const ep of endpoints) {
    if (!groups[ep.dir]) {
      groups[ep.dir] = { label: ep.tag, items: [] };
    }
    groups[ep.dir].items.push(ep);
  }

  const lines = [START_MARKER, ''];

  // Sort domains alphabetically for consistency
  const sortedDirs = Object.keys(groups).sort();
  for (const dir of sortedDirs) {
    const group = groups[dir];
    lines.push(`## ${group.label}`, '');
    for (const item of group.items) {
      lines.push(`* [${item.title}](${dir}/${item.fileName})`);
    }
    lines.push('');
  }

  lines.push(END_MARKER);
  return lines.join('\n');
}

function mergeSummary(outputDir, endpoints) {
  if (endpoints.length === 0) return;

  const summaryPath = path.join(outputDir, 'SUMMARY.md');
  const autoSection = buildAutoSection(endpoints);

  if (!fs.existsSync(summaryPath)) {
    // No existing SUMMARY.md — create one with auto section
    const content = `# Table of contents\n\n* [소개](README.md)\n\n${autoSection}\n`;
    fs.writeFileSync(summaryPath, content, 'utf-8');
    return;
  }

  const existing = fs.readFileSync(summaryPath, 'utf-8');
  const startIdx = existing.indexOf(START_MARKER);
  const endIdx = existing.indexOf(END_MARKER);

  let merged;
  if (startIdx !== -1 && endIdx !== -1) {
    // Replace existing auto section
    const before = existing.substring(0, startIdx);
    const after = existing.substring(endIdx + END_MARKER.length);
    merged = before + autoSection + after;
  } else {
    // Append auto section at the end
    merged = existing.trimEnd() + '\n\n' + autoSection + '\n';
  }

  fs.writeFileSync(summaryPath, merged, 'utf-8');
}

module.exports = { mergeSummary };
