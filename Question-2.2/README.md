# Question 2.2: Array Manipulation and Missing number problem

## Algorithm
Since the array contains only **distinct numbers from [1, n+1]** we know that the sum without any missing number is $\frac{(n + 1)(n + 2)}{2}$.

We can calculate the **actual sum** then do a **substraction** to get the missing number.
```plaintext
3, 7, 1, 2, 6, 4
   1, 2, 3,4,5,6, 7, 8, 10 11, 12, 13, 14, 15, 16
2 ,  , 9 , 3 , 4, 6, 5, 1 ,  7
2 , 8, 9 , 3 , 4, 6, 5, 1 ,  7

761, 930, 833, 782, 954, 293, 806, 43, 897, 942, 181, 240, 99, 959, 811, 448, 91, 253, 90, 166, 103, 236, 76, 277, 777, 19, 527, 876, 507, 678, 674, 143, 960, ...
8, 1, 3, 5, 6, 9, 4, 7, 2
1, 2, 3, 4, 5, 6, 7, 8,
2 , 3, 9 , 3 , 4, 6, 5, 1 ,  7
3 2 1
1 , 2 , 4
```
```plaintext
 1 | N = 6 -> Missing number = 5
 2 | input.txt:2:27: error: Expect ','.
   |  7, 8, 10 11, 12, 13
   |          ^~~~~
 3 | input.txt:3:5: error: Expect a number possitive number.
   | 2 ,  , 9 , 3 , 4, 6,
   |     ^~~~~
 4 | N = 9 -> Missing number = 10
 5 | N = 0 -> Missing number = 1
 6 | N = 1000 -> Missing number = 503
 7 | N = 9 -> Missing number = 10
 8 | N = 8 -> Missing number = 9
 9 | N = 9 -> Missing number = 15
10 | input.txt:10:2: error: Expect ','.
   | 3 2 1
   |  ^~~~~
11 | N = 3 -> Missing number = 3
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
