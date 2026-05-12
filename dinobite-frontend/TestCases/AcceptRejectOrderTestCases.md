# Use Case: Courier Accepts or Rejects Order

## Test Case 1: Courier Accepts Assigned Order
- **Input**: Courier clicks "Accept" on an assigned order.
- **Expected Result**: Order status changes to "Ready For Pickup" and is visible on restaurant page.
- **Status**: PASSED

## Test Case 2: Courier Rejects Assigned Order
- **Input**: Courier clicks "Reject" on an assigned order.
- **Expected Result**: Order status changes to "Preparing", restaurant is prompted to assign a new courier.
- **Status**: PASSED

## Test Case 3: Invalid Courier Token
- **Input**: Unauthorized courier tries to respond to order.
- **Expected Result**: They can't.
- **Status**: PASSED

## Test Case 4: Order Already Accepted by Another Courier
- **Input**: Courier tries to accept an order already taken.
- **Expected Result**: System displays error message and disables button.
- **Status**: FAILED

## Test Case 5: Real-Time Update on Restaurant Dashboard
- **Input**: Courier accepts/rejects order.
- **Expected Result**: Status change reflected immediately on restaurant dashboard.
- **Status**: PASSED
