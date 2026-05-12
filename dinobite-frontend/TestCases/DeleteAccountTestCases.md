# Use Case: Delete Account

## Test Case 1: Successful Account Deletion
- **Input**: Logged-in user clicks "Delete Account" and confirms in the confirmation dialog.
- **Expected Result**: User account is deleted, user is logged out and redirected to login page.
- **Status**: PASSED

## Test Case 2: Cancel Deletion
- **Input**: User clicks "Delete Account" but cancels the confirmation.
- **Expected Result**: Account remains active, no changes made.
- **Status**: PASSED

## Test Case 3: Unauthorized Deletion Attempt
- **Input**: Unauthenticated user tries to access account deletion endpoint.
- **Expected Result**: User cannot access the button.
- **Status**: PASSED

## Test Case 4: Session Expired During Deletion
- **Input**: User opens the "Delete Account" page but session expires before confirmation.
- **Expected Result**: User is asked to log in again before proceeding.
- **Status**: PASSED
