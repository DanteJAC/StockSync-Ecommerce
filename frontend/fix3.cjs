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
  
  content = content.replace(/<v-row([^>]*)>/g, (match, attrs) => {
    if (attrs.includes('mx-0')) return match;
    
    if (attrs.includes('class="')) {
      return `<v-row${attrs.replace('class="', 'class="mx-0 ')}>`;
    } else {
      return `<v-row class="mx-0"${attrs}>`;
    }
  });
  
  // also fix container if they use fluid without padding adjustments sometimes it overflows
  // but row mx-0 should fix it
  
  if (content !== original) {
    fs.writeFileSync(file, content, 'utf8');
    console.log('Fixed v-row:', file);
  }
});
