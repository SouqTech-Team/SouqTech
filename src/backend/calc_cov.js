const fs = require('fs');
const readline = require('readline');

const fileStream = fs.createReadStream('target/site/jacoco/jacoco.csv');

const rl = readline.createInterface({
    input: fileStream,
    crlfDelay: Infinity
});

let totalMissed = 0;
let totalCovered = 0;
let isFirstLine = true;

rl.on('line', (line) => {
    if (isFirstLine) {
        isFirstLine = false;
        return;
    }
    const parts = line.split(',');
    if (parts.length > 4) {
        totalMissed += parseInt(parts[3], 10);
        totalCovered += parseInt(parts[4], 10);
    }
});

rl.on('close', () => {
    const total = totalMissed + totalCovered;
    const percent = total > 0 ? (totalCovered / total) * 100 : 0;
    console.log(`Total Instructions: ${total}`);
    console.log(`Covered: ${totalCovered}`);
    console.log(`Missed: ${totalMissed}`);
    console.log(`Coverage: ${percent.toFixed(2)}%`);
});
