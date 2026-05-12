# Use Case: User Logout

## Test Case 1: Successful Logout
- **Input**: Click on "Logout" button while logged in.
- **Expected Result**: User session is cleared, redirected to login or home page.
- **Status**: PASSED

## Test Case 2: Logout Without Active Session
- **Input**: Access logout endpoint without logging in.
- **Expected Result**: Redirected to login page or shown appropriate message like "Session expired".
- **Status**: PASSED

## Test Case 3: Double Logout
- **Input**: Click "Logout" twice or try logout after already logging out.
- **Expected Result**: Handled gracefully; no crash or error, stays on login page.
- **Status**: PASSED
