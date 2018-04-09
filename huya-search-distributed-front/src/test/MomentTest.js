var moment = require('moment');
let momentObject = moment();
console.log("format = " + momentObject.format());
console.log("day = " + momentObject.day());
console.log("month = " + momentObject.month());
console.log("hour = " + momentObject.add(-1, 'hours').hour());
console.log("minute = " + momentObject.minute());
console.log("second = " + momentObject.second());

console.log("yyyy = " + momentObject.format("YYYY-MM-DD HH:00:00"));