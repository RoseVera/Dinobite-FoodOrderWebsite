# Use Case: Manage Orders (Restaurant Perspective)

## Test Case 1: Change Order Status to "Preparing"
- **Input**: Restaurant updates order status from "Pending" to "Preparing".
- **Expected Result**: Status is updated, and the customer sees the change in real-time.
- **Status**: PASSED

## Test Case 2: Change Order Status to "Ready"
- **Input**: Restaurant updates status from "Preparing" to "Ready For Pickup".
- **Expected Result**: Status is saved, and shown correctly to the courier and customer.
- **Status**: PASSED

## Test Case 3: Assign Courier to an Order
- **Input**: Restaurant selects an available courier for a ready order.
- **Expected Result**: courier is assigned successfully and notified.
- **Status**: PASSED

## Test Case 4: Attempt to Assign Order Already Assigned
- **Input**: Restaurant tries to assign a courier to an already assigned order.
- **Expected Result**: System prevents re-assignment and shows warning.
- **Status**: PASSED

## Test Case 5: Filter Orders
- **Input**: Restaurant filters orders(e.g., only preparing, ready for pickup)
- **Expected Result**: appropriate order list shown
- **Status**: PASSED