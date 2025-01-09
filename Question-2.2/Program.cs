using System.Diagnostics.CodeAnalysis;
using System.IO;
using System;

string filePath = "input.txt";
string[] lines = File.ReadAllLines(filePath);
for (int i = 0; i < lines.Length; i++) {
    if (findMissingNumber(lines[i], i+1, out long missingNumber, out int size)) {
        Console.WriteLine($"{i+1} | N = {size} -> Missing number = {missingNumber}");
    }
}

bool findMissingNumber(in ReadOnlySpan<char> line, int lineNumber, out long missingNumber, out int size) {
    missingNumber = -1;
    size = 0;

    ReadOnlySpan<char> data = line;
    bool expectNumber = true;
    int col = trimSpaceStart(ref data);
    long sum = 0;

    while (col < line.Length) {
        if (expectNumber) {
            if (Char.IsDigit(data[0])) {
                int numberLength = getNumber(ref data, out long result);
                if (result <= 0) {
                    reportError(line, lineNumber, col, "Expect a possitive number.");
                    return false;
                } else {
                    expectNumber = false;
                    col += numberLength;
                    sum += result;
                    size++;
                }
            } else {
                reportError(in line, lineNumber, col, "Expect a number possitive number.");
                return false;
            }
        } else {
            if (data[0] == ',') {
                col += 1;
                data = data.Slice(1);
                expectNumber = true;
            } else {
                reportError(in line, lineNumber, col, "Expect ','.");
                return false;
            }
        }
        col += trimSpaceStart(ref data);
    }
    missingNumber = ((size+1 + 1)*(size+1)/2) - sum;
    return true;
}

int getNumber(ref ReadOnlySpan<char> data, out long result) {
    int i = 0;
    while (i < data.Length && Char.IsDigit(data[i])) i++;
    result = long.Parse(data.Slice(0, i));
    data = data.Slice(i);
    return i;
}

int trimSpaceStart(ref ReadOnlySpan<char> line) {
    int i = 0;
    while (i < line.Length && Char.IsWhiteSpace(line[i])) i++;
    line = line.Slice(i);
    return i;
}

void reportError(in ReadOnlySpan<char> line, int lineNumber, int col, ReadOnlySpan<char> error) {
    ConsoleColor originalColor = Console.ForegroundColor;
    Console.ForegroundColor = ConsoleColor.Red;
    Console.WriteLine($"{lineNumber} | {filePath}:{lineNumber}:{col}: error: {error}");
    int start = int.Max(col - 10, 0);
    int length = int.Min(line.Length - col, 20);
    Console.Error.WriteLine($"  | {line.Slice(start, length)}");
    Console.Error.WriteLine($"  | {"^".PadLeft(col-start)}~~~~");
    Console.ForegroundColor = originalColor;
}
