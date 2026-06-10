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
  
  // Fix tables
  content = content.replace(/<v-table>/g, '<v-table class="text-no-wrap">');
  
  // Fix big titles
  content = content.replace(/class="([^"]*)text-h4([^"]*)"/g, 'class="$1text-h5 text-md-h4$2"');
  
  // Fix overflowing cards (basic word break)
  content = content.replace(/<v-card-title>/g, '<v-card-title class="text-wrap">');
  
  if (content !== original) {
    fs.writeFileSync(file, content, 'utf8');
    console.log('Fixed:', file);
  }
});
