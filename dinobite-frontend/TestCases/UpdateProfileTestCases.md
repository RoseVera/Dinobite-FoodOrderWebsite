# Use Case: Update Profile Information (Customer / Restaurant / Courier)

## Test Case 1: Successfully Update Basic Information
- **Input**: User updates name, email, and phone number with valid values.
- **Expected Result**: Profile information is updated and reflected immediately.
- **Status**: PASSED

## Test Case 2: Attempt Update with Invalid Email Format
- **Input**: User enters an invalid email (e.g., "example@.com").
- **Expected Result**: System blocks the update and shows a validation error.
- **Status**: PASSED

## Test Case 3: Attempt Update with Empty Required Fields
- **Input**: User clears name or email and submits.
- **Expected Result**: System prevents submission and displays error.
- **Status**: PASSED

## Test Case 4: Update with Same Information (No Change)
- **Input**: User submits profile form without making any changes.
- **Expected Result**: System does nothing.
- **Status**: PASSED
