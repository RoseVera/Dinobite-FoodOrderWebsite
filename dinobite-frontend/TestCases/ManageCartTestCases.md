# Use Case: Place Order & Manage Cart

## Test Case 1: Add Items to Cart
- **Input**: User adds two different food items from the same restaurant to the cart.
- **Expected Result**: Both items appear in the cart with correct quantities and prices.
- **Status**: PASSED

## Test Case 2: Increase Item Quantity
- **Input**: User increases the quantity of an item in the cart from 1 to 3.
- **Expected Result**: The quantity is updated, and total price is recalculated correctly.
- **Status**: PASSED

## Test Case 3: Remove Item from Cart
- **Input**: User removes an item from the cart.
- **Expected Result**: Item is removed and the total price updates accordingly.
- **Status**: PASSED

## Test Case 4: Place Order with Valid Cart
- **Input**: User proceeds to checkout and confirms the order.
- **Expected Result**: Order is placed successfully and confirmation message is shown.
- **Status**: PASSED

## Test Case 5: Try to Place Order with Empty Cart
- **Input**: User tries to go to checkout with an empty cart.
- **Expected Result**: Cant see proceed to checkout button.
- **Status**: PASSED

## Test Case 6: Increase Item Quantity Over 100
- **Input**: User increases the quantity of an item in the cart from 1 to 100.
- **Expected Result**: System says user cant have that much item in one order.
- **Status**: FAILED

## Test Case 7: Proceed To Checkout Without Customer Login
- **Input**: Not logged in as a customer user clicks proceed to checkout button.
- **Expected Result**: "To order, you must be logged in." message shown.
- **Status**: PASSED