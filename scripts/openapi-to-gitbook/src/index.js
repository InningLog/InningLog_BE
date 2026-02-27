#!/usr/bin/env node
'use strict';

const fs = require('fs');
const path = require('path');
const { convert } = require('./converter');

function parseArgs() {
  const args = process.argv.slice(2);
  const opts = {};
  for (let i = 0; i < args.length; i += 2) {
    if (args[i] === '-i') opts.input = args[i + 1];
    else if (args[i] === '-o') opts.output = args[i + 1];
  }
  return opts;
}

function main() {
  const opts = parseArgs();
  if (!opts.input || !opts.output) {
    console.error('Usage: node src/index.js -i <openapi.json> -o <output-dir>');
    process.exit(1);
  }

  const specPath = path.resolve(opts.input);
  if (!fs.existsSync(specPath)) {
    console.error(`Input file not found: ${specPath}`);
    process.exit(1);
  }

  const spec = JSON.parse(fs.readFileSync(specPath, 'utf-8'));
  const result = convert(spec, path.resolve(opts.output));

  console.log(`Generated ${result.filesWritten} files in ${result.domains} domains`);
  if (result.skipped > 0) {
    console.log(`Skipped ${result.skipped} existing manual files`);
  }
}

main();
