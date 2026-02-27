'use strict';

const AUTO_MARKER = '<!-- AUTO-GENERATED -->';

function resolveRef(ref, spec) {
  const parts = ref.replace('#/', '').split('/');
  let result = spec;
  for (const part of parts) {
    result = result?.[part];
    if (!result) return null;
  }
  return result;
}

function resolveSchema(schema, spec, depth) {
  if (!schema || depth > 8) return schema;
  if (schema.$ref) {
    return resolveSchema(resolveRef(schema.$ref, spec), spec, depth + 1);
  }
  if (schema.allOf) {
    const merged = { type: 'object', properties: {}, required: [] };
    for (const sub of schema.allOf) {
      const resolved = resolveSchema(sub, spec, depth + 1);
      if (resolved?.properties) Object.assign(merged.properties, resolved.properties);
      if (resolved?.required) merged.required.push(...resolved.required);
    }
    return merged;
  }
  return schema;
}

function getTypeString(schema, spec) {
  if (!schema) return 'Object';

  const resolved = resolveSchema(schema, spec, 0);
  if (!resolved) return 'Object';

  if (resolved.type === 'array') {
    const items = resolveSchema(resolved.items, spec, 0);
    return `${getTypeString(items, spec)}[]`;
  }

  if (resolved.type === 'string' && resolved.enum) {
    return `String (${resolved.enum.join(', ')})`;
  }

  if (resolved.format === 'int64') return 'Long';
  if (resolved.format === 'int32') return 'Integer';
  if (resolved.format === 'date') return 'LocalDate';
  if (resolved.format === 'date-time') return 'LocalDateTime';

  const typeMap = {
    string: 'String',
    integer: 'Integer',
    number: 'Number',
    boolean: 'Boolean',
    object: 'Object',
  };

  return typeMap[resolved.type] || resolved.type || 'Object';
}

function flattenProperties(schema, spec, prefix, depth) {
  if (!schema || depth > 3) return [];

  const resolved = resolveSchema(schema, spec, 0);
  if (!resolved?.properties) return [];

  const rows = [];
  const required = new Set(resolved.required || []);

  for (const [name, prop] of Object.entries(resolved.properties)) {
    const fieldName = prefix ? `${prefix}.${name}` : name;
    const resolvedProp = resolveSchema(prop, spec, 0);
    const type = getTypeString(prop, spec);
    const isRequired = required.has(name);
    const description = escapePipe(resolvedProp?.description || '');
    const example = resolvedProp?.example !== undefined
      ? escapePipe(String(resolvedProp.example))
      : '';

    rows.push({ fieldName, type, isRequired, description, example });

    if (resolvedProp?.type === 'object' && resolvedProp?.properties) {
      rows.push(...flattenProperties(resolvedProp, spec, fieldName, depth + 1));
    }

    if (resolvedProp?.type === 'array' && resolvedProp?.items) {
      const itemSchema = resolveSchema(resolvedProp.items, spec, 0);
      if (itemSchema?.type === 'object' && itemSchema?.properties) {
        rows.push(...flattenProperties(itemSchema, spec, `${fieldName}[]`, depth + 1));
      }
    }
  }

  return rows;
}

function escapePipe(str) {
  return str.replace(/\|/g, '\\|').replace(/\n/g, ' ');
}

function isAuthRequired(operation, spec) {
  if (operation.security !== undefined) {
    return operation.security.length > 0;
  }
  return !!(spec.security && spec.security.length > 0);
}

function generateEndpointMarkdown(method, pathStr, operation, spec) {
  const lines = [AUTO_MARKER, ''];
  const upperMethod = method.toUpperCase();

  // 제목
  lines.push(`## ${operation.summary || `${upperMethod} ${pathStr}`}`, '');

  // 개요
  lines.push('### 개요', '');
  if (operation.description) {
    lines.push(operation.description.trim(), '');
  } else {
    lines.push(`${upperMethod} ${pathStr}`, '');
  }

  // 엔드포인트
  lines.push('### 엔드포인트', '');
  lines.push(`\`${upperMethod} ${pathStr}\``, '');

  // 인증
  const requiresAuth = isAuthRequired(operation, spec);
  lines.push('### 인증', '');
  lines.push(requiresAuth ? '🔒 JWT 인증 필요' : '🔓 인증 불필요', '');

  // 요청 (Request)
  lines.push('### 요청 (Request)', '');

  // Headers
  lines.push('#### Headers', '');
  lines.push('| 헤더 | 값 | 필수 |');
  lines.push('|------|------|------|');
  if (requiresAuth) {
    lines.push('| Authorization | Bearer {token} | ✅ |');
  }
  if (operation.requestBody) {
    const contentType = operation.requestBody.content?.['multipart/form-data']
      ? 'multipart/form-data'
      : 'application/json';
    lines.push(`| Content-Type | ${contentType} | ✅ |`);
  }
  lines.push('');

  // Path Parameters
  const pathParams = (operation.parameters || []).filter(p => p.in === 'path');
  if (pathParams.length > 0) {
    lines.push('#### Path Parameters', '');
    lines.push('| 파라미터 | 타입 | 필수 | 설명 | 예시 |');
    lines.push('|----------|------|------|------|------|');
    for (const param of pathParams) {
      const type = getTypeString(param.schema || {}, spec);
      const req = param.required ? '✅' : '';
      const desc = escapePipe(param.description || '');
      const ex = param.example ?? param.schema?.example ?? '';
      lines.push(`| ${param.name} | ${type} | ${req} | ${desc} | ${ex} |`);
    }
    lines.push('');
  }

  // Query Parameters
  const queryParams = (operation.parameters || []).filter(p => p.in === 'query');
  if (queryParams.length > 0) {
    lines.push('#### Query Parameters', '');
    lines.push('| 파라미터 | 타입 | 필수 | 설명 | 예시 |');
    lines.push('|----------|------|------|------|------|');
    for (const param of queryParams) {
      const type = getTypeString(param.schema || {}, spec);
      const req = param.required ? '✅' : '';
      const desc = escapePipe(param.description || '');
      const ex = param.example ?? param.schema?.example ?? '';
      lines.push(`| ${param.name} | ${type} | ${req} | ${desc} | ${ex} |`);
    }
    lines.push('');
  }

  // Request Body
  if (operation.requestBody) {
    const content = operation.requestBody.content?.['application/json']
      || operation.requestBody.content?.['multipart/form-data'];

    if (content?.schema) {
      const fields = flattenProperties(content.schema, spec, '', 0);
      if (fields.length > 0) {
        lines.push('#### Body', '');
        lines.push('| 필드명 | 타입 | 필수 | 설명 | 예시 |');
        lines.push('|--------|------|------|------|------|');
        for (const f of fields) {
          lines.push(`| ${f.fieldName} | ${f.type} | ${f.isRequired ? '✅' : ''} | ${f.description} | ${f.example} |`);
        }
        lines.push('');
      }
    }

    // 요청 예시
    const examples = content?.examples;
    if (examples) {
      const first = Object.values(examples)[0];
      if (first?.value) {
        lines.push('#### 요청 예시', '');
        lines.push('```json');
        lines.push(JSON.stringify(first.value, null, 2));
        lines.push('```', '');
      }
    }
  }

  // 응답 (Response) - 2xx만
  lines.push('### 응답 (Response)', '');

  const responses = operation.responses || {};
  const successCodes = Object.keys(responses).filter(c => c.startsWith('2'));
  const errorCodes = Object.keys(responses).filter(c => !c.startsWith('2'));

  for (const code of successCodes) {
    const res = responses[code];
    const resContent = res.content?.['application/json'];

    lines.push(`#### ${code} ${escapePipe(res.description || '')}`, '');

    if (resContent?.schema) {
      const fields = flattenProperties(resContent.schema, spec, '', 0);
      if (fields.length > 0) {
        lines.push('| 필드명 | 타입 | 설명 |');
        lines.push('|--------|------|------|');
        for (const f of fields) {
          lines.push(`| ${f.fieldName} | ${f.type} | ${f.description} |`);
        }
        lines.push('');
      }
    }

    if (resContent?.examples) {
      const first = Object.values(resContent.examples)[0];
      if (first?.value) {
        lines.push('#### 응답 예시', '');
        lines.push('```json');
        lines.push(JSON.stringify(first.value, null, 2));
        lines.push('```', '');
      }
    }
  }

  // 실패 (Error) - 비-2xx
  if (errorCodes.length > 0) {
    lines.push('### 실패 (Error)', '');
    lines.push('| 코드 | 설명 |');
    lines.push('|------|------|');
    for (const code of errorCodes) {
      const desc = escapePipe(responses[code].description || '');
      lines.push(`| ${code} | ${desc} |`);
    }
    lines.push('');
  }

  return lines.join('\n');
}

module.exports = { generateEndpointMarkdown, AUTO_MARKER };
