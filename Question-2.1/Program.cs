using System;
using System.IO;
using System.Collections;
using System.Text.RegularExpressions;
using System.Diagnostics.CodeAnalysis;

if (args.Length != 1 || !File.Exists(args[0]) || File.GetAttributes(args[0]).HasFlag(FileAttributes.Directory)) {
    Console.WriteLine("Usage: dotnet run <input_file>");
    return 1;
}
string filePath = args[0];
string[] lines = File.ReadAllLines(filePath);
int lineNumberMaxLength = (int)(Math.Log10(lines.Length)) + 1;
List<Product>? result = parseProductInventory(in lines);
if (result == null) return 1;

Console.WriteLine("Total inventory value: {0}", totalInventoryValue(result));
Console.WriteLine("Most expensive product: {0}", mostExpensiveProduct(result).name);
Console.WriteLine("Has product 'Headphones': {0}", checkForProduct(result, "Headphones"));
return 0;




double totalInventoryValue(List<Product> products) => products.Aggregate(0.0, (acc, product) => acc + product.price * product.quantity);
Product mostExpensiveProduct(List<Product> products) => products.OrderByDescending(product => product.price).First();
bool checkForProduct(List<Product> products, string name) => result.Any(product => product.name == name);

List<Product>? parseProductInventory(in string[] lines) {
    if (lines[0].StartsWith("Product List:")) {
        if (lines[0].Length != "Product List:".Length) {
            reportError(lines[0], 1, "Product List:".Length+1, "trailing white space");
            return null;
        }
    } else {
        reportError(lines[0], 1, 0, "first line must start be 'Product List:'");
        return null;
    }

    List<Product> result = new();
    int lineNumber = 1;
    foreach (string line in lines[1..]) {
        lineNumber++;
        if (String.IsNullOrWhiteSpace(line)) continue;
        Product? product = parseProduct(line, lineNumber);
        if (product != null) {
            result.Add(product);
        }
    }
    Console.WriteLine("------------------------------------------------------------------");
    return result;
}

// parse and report input error
Product? parseProduct(in string line, int lineNumber) {
    string productPattern = @"([a-zA-Z0-9\s]+):\s*price\s*(\d*(\.\d*)?),\s*quantity\s*(\d+)";
    Match match = Regex.Match(line, productPattern);
    if (match.Success) {
        string product = match.Groups[1].Value;
        double price = double.Parse(match.Groups[2].Value);
        uint quantity = uint.Parse(match.Groups[4].Value);
        Console.WriteLine($"Product Name: {product}, Price: {price}, Quantity: {quantity}");
        return new(product, price, quantity);
    } else {
        reportError(line, lineNumber, 0, "does not match the input format");
    }
    return null;
}

void reportError(in ReadOnlySpan<char> line, int lineNumber, int col, ReadOnlySpan<char> error) {
    col++; // passed in as 0-based
    ConsoleColor originalColor = Console.ForegroundColor;
    Console.ForegroundColor = ConsoleColor.Red;
    Console.WriteLine($"{lineNumber.ToString().PadLeft(lineNumberMaxLength)} | {filePath}:{lineNumber}:{col}: error: {error}");
    int start = int.Max(col - 50, 0);
    int length = int.Min(line.Length - start, 100);
    Console.Error.WriteLine($"{" ".PadLeft(lineNumberMaxLength+1)}| {(start != 0 ? "...":"")}{line.Slice(start, length)}{(start+length < line.Length ? "...":"")}");
    Console.Error.WriteLine($"{" ".PadLeft(lineNumberMaxLength+1)}| {(start != 0 ? "   ":"")}{"^".PadLeft(col-start)}~~~~");
    Console.ForegroundColor = originalColor;
}

class Product {
    public string name;
    public double price;
    public uint quantity;
    public Product(string name, double price, uint quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }
}
