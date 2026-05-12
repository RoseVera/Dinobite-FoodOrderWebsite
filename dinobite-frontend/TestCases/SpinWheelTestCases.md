# Use Case: Spin Wheel (Weekly Coupon Game)

## Test Case 1: Customer Spins Wheel Successfully
- **Input**: Logged-in customer clicks "Spin the Wheel" button.
- **Expected Result**: The wheel spins and lands on a random coupon, which is then added to the user's account.
- **Status**: PASSED

## Test Case 2: Unauthorized User Tries to Spin
- **Input**: Non-customer (e.g., restaurant or courier) tries to access the spin wheel feature.
- **Expected Result**: Access is denied with appropriate message.
- **Status**: FAILED
- **Notes**: Access is denied but just cant spin the wheel doesn't tell the reason.

## Test Case 3: Customer Tries to Spin More Than Once Per Week
- **Input**: Same customer tries to spin the wheel again before a full week has passed.
- **Expected Result**: System blocks the action and shows message indicating next available spin date.
- **Status**: FAILED
- **Notes**: System blocks the action but just gives an error not an explanation.

## Test Case 4: Spin Wheel Result is Persisted
- **Input**: Customer refreshes the page after spinning.
- **Expected Result**: Coupon won remains visible in account and not re-spinnable.
- **Status**: PASSED

## Test Case 5: Coupon Validity After Winning
- **Input**: Customer views won coupon.
- **Expected Result**: Coupon has a validity period and discount details are correctly shown.
- **Status**: FAILED
- **Notes**: Coupon added to the database but not shown in user page.
