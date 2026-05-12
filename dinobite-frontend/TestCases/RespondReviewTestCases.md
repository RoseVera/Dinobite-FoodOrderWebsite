# Use Case: Respond to Customer Reviews (Restaurant Perspective)

## Test Case 1: Respond to a Valid Review
- **Input**: Restaurant adds a response to a customer's review on a completed order.
- **Expected Result**: Response is saved and displayed under the customer’s review.
- **Status**: PASSED

## Test Case 2: Attempt to Respond to a Review They Respondended Before
- **Input**: Restaurant tries to respond to a review again.
- **Expected Result**: System blocks the action and shows an appropriate error message.
- **Status**: PASSED

## Test Case 3: Respond With Invalid Content (e.g., empty)
- **Input**: Restaurant attempts to submit an empty or inappropriate response.
- **Expected Result**: System validates input and prevents submission.
- **Status**: PASSED
