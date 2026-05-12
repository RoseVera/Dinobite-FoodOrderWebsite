# UC-001: User Registration

## Test Case 1: Successful Registration
# Test Case 1.1: Successful Customer Registration
- **Input**: Valid name, email, password, city, address, phone number and birth date.
- **Expected Result**: Customer account created successfully, redirected to homepage.
- **Status**: PASSED
- **Notes**: All validations passed and new user/customer appears in the database.

# Test Case 1.2: Successful Restaurant Registration
- **Input**: Valid restaurant name, email, password, city, address, owner mail, phone number, opening hours, cuisine, delivery range, photo url.
- **Expected Result**: Restaurant account created successfully, redirected to restaurant dashboard
- **Status**: PASSED
- **Notes**: All validations passed and new user/restaurant appears in the database.

# Test Case 1.3: Successful Courier Registration
- **Input**: Valid name, email, password, city, phone number, birth date, photo url.
- **Expected Result**: Courier account created successfully, redirected to restaurant dashboard
- **Status**: PASSED
- **Notes**: All validations passed and new user/courier appears in the database.

## Test Case 2: Email Already Exists  (getByEmail yaz user controller a)
- **Input**: Email that is already in use, valid other fields.
- **Expected Result**: Error message displayed: "Email already in use".
- **Status**: FAILED
- **Notes**: Proper backend validation triggered but error message not shown.

## Test Case 3: Invalid Email Format
- **Input**: Incorrect email format (e.g., "user@com"), valid other fields.
- **Expected Result**: Error message shown: "Invalid email format".
- **Status**: FAILED
- **Notes**: Proper backend validation triggered but error message not shown.

## Test Case 4: Invalid Phone Format
- **Input**: Incorrect phone format (e.g., "11111111"), valid other fields.
- **Expected Result**: Error message shown: "Invalid phone format".
- **Status**: FAILED
- **Notes**: Proper backend validation triggered but error message not shown.

## Test Case 5: Invalid Delivery Range
- **Input**: Incorrect delivery range (e.g., "250"), valid other fields.
- **Expected Result**: Error message shown: "Invalid Delivery Range".
- **Status**: FAILED
- **Notes**: Proper backend validation triggered but error message not shown.

## Test Case 6: Not Agree KVKK
- **Input**: Not clicking the KVKK button, valid other fields.
- **Expected Result**: Error message shown: "Please accept KVKK Agreement".
- **Status**: PASSED
- **Notes**: Proper backend validation triggered and error message shown.