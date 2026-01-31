// mongo-init.js
db = db.getSiblingDB('admin');

db.createUser({
  user: 'catalog_user',
  pwd: 'catalog_pass',
  roles: [
    {
      role: 'readWrite',
      db: 'catalog_db'
    }
  ]
});

// สร้าง database และ collection เริ่มต้น
db = db.getSiblingDB('catalog_db');
db.createCollection('products');