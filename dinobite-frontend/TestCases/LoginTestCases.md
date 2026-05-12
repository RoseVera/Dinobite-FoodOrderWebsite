# Use Case: User Login

## Test Case 1: Successful Login
# Test Case 1.1: Successful Customer Login
- **Input**: Valid email and correct password.
- **Expected Result**: Redirected to homepage.
- **Status**: PASSED
- **Notes**: User session created successfully.

# Test Case 1.2: Successful Restaurant Login
- **Input**: Valid email and correct password.
- **Expected Result**: Redirected to restaurant dashboard.
- **Status**: PASSED
- **Notes**: User session created successfully.

# Test Case 1.3: Successful Courier Login
- **Input**: Valid email and correct password.
- **Expected Result**: Redirected to courier dashboard.
- **Status**: PASSED
- **Notes**: User session created successfully.

## Test Case 2: Wrong Password
- **Input**: Correct email, incorrect password.
- **Expected Result**: Error message displayed: "Email or password is incorrect".
- **Status**: PASSED

## Test Case 3: Non-existing Email
- **Input**: Email not found in database, random password.
- **Expected Result**: Error message: "Email or password is incorrect".
- **Status**: PASSED

## Test Case 4: Empty Fields
- **Input**: Leave email and/or password blank.
- **Expected Result**: Validation errors shown for missing fields.
- **Status**: PASSED

## Test Case 5: Invalid Email Format
- **Input**: Email without "@" or domain (e.g., "chekmail").
- **Expected Result**: Validation error shown for email field.
- **Status**: PASSED
