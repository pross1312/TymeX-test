# Question 2.2: Array Manipulation and Missing number problem

## Demo
```plaintext
Product List:
Laptop: price .99, quantity 5
Laptop: price 999.99, quantity 5

Smartphone: price 499.99, quantity 10
Smartwatch: 
Tablet: price 299.99, quantity 0
Smartwatch: price 199.99, quantity 3
```
```plaintext
Product Name: Laptop, Price: 0.99, Quantity: 5
Product Name: Laptop, Price: 999.99, Quantity: 5
Product Name: Smartphone, Price: 499.99, Quantity: 10
6 | .\input.txt:6:1: error: does not match the input format
  | Smartwatch:
  | ^~~~~
Product Name: Tablet, Price: 299.99, Quantity: 0
Product Name: Smartwatch, Price: 199.99, Quantity: 3
------------------------------------------------------------------
Total inventory value: 10604.769999999999
Most expensive product: Laptop
Has product 'Headphones': False
```

## Prerequisites

1. **Install .NET SDK or later**
- Download and install the .NET SDK from [Microsoft .NET Download Page](https://dotnet.microsoft.com/download).
- Verify installation:
    ```bash
    dotnet --version
    ```
    Ensure the output is `8.x.x` or higher.

### Quickstart
```bash
dotnet run input.txt
```
