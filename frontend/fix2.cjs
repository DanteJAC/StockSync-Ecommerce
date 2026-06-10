const fs = require('fs');
const path = require('path');
function walk(dir) {
  let results = [];
  const list = fs.readdirSync(dir);
  list.forEach(file => {
    file = path.join(dir, file);
    const stat = fs.statSync(file);
    if (stat && stat.isDirectory()) { 
      results = results.concat(walk(file));
    } else if (file.endsWith('.vue')) { 
      results.push(file);
    }
  });
  return results;
}
const files = walk('./src/views');
files.forEach(file => {
  let content = fs.readFileSync(file, 'utf8');
  let original = content;
  
  // Find all <v-table ...> tags
  content = content.replace(/<v-table([^>]*)>/g, (match, attrs) => {
    if (attrs.includes('text-no-wrap')) return match;
    
    if (attrs.includes('class="')) {
      return `<v-table${attrs.replace('class="', 'class="text-no-wrap ')}>`;
    } else {
      return `<v-table class="text-no-wrap"${attrs}>`;
    }
  });
  
  if (content !== original) {
    fs.writeFileSync(file, content, 'utf8');
    console.log('Fixed:', file);
  }
});
