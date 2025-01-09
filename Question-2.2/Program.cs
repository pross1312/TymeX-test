using System.Diagnostics.CodeAnalysis;
using System.IO;
using System;

if (args.Length != 1 || !File.Exists(args[0]) || File.GetAttributes(args[0]).HasFlag(FileAttributes.Directory)) {
    Console.WriteLine("Usage: dotnet run <input_file>");
    return 1;
}
string filePath = args[0];
string[] lines = File.ReadAllLines(filePath);
int lineNumberMaxLength = (int)(Math.Log10(lines.Length)) + 1;
for (int i = 0; i < lines.Length; i++) {
    if (findMissingNumber(lines[i], i+1, out ulong missingNumber, out ulong size)) {
        Console.WriteLine($"{(i+1).ToString().PadLeft(lineNumberMaxLength)} | N = {size} -> Missing number = {missingNumber}");
    }
}
return 0;

bool findMissingNumber(in ReadOnlySpan<char> line, int lineNumber, out ulong missingNumber, out ulong size) {
    missingNumber = 0;
    size = 0;

    ReadOnlySpan<char> data = line;
    bool expectNumber = true;
    int col = trimSpaceStart(ref data);
    ulong sum = 0;

    while (col < line.Length) {
        if (expectNumber) {
            if (Char.IsDigit(data[0])) {
                int numberLength = getNumber(ref data, out ulong result);
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

int getNumber(ref ReadOnlySpan<char> data, out ulong result) {
    int i = 0;
    while (i < data.Length && Char.IsDigit(data[i])) i++;
    result = ulong.Parse(data.Slice(0, i));
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
    col += 1; // passed in as 0-based
    ConsoleColor originalColor = Console.ForegroundColor;
    Console.ForegroundColor = ConsoleColor.Red;
    Console.WriteLine($"{lineNumber.ToString().PadLeft(lineNumberMaxLength)} | {filePath}:{lineNumber}:{col}: error: {error}");
    int start = int.Max(col - 20, 0);
    int length = int.Min(line.Length - start, 40);
    Console.Error.WriteLine($"{" ".PadLeft(lineNumberMaxLength+1)}| {(start != 0 ? "...":"")}{line.Slice(start, length)}{(start+length < line.Length ? "...":"")}");
    Console.Error.WriteLine($"{" ".PadLeft(lineNumberMaxLength+1)}| {(start != 0 ? "   ":"")}{"^".PadLeft(col-start)}~~~~");
    Console.ForegroundColor = originalColor;
}
