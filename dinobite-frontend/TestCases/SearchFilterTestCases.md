# Use Case: Search and Filter Restaurants

## Test Case 1: Search by Minimum Order Amount
- **Input**: User sets the minimum order filter to "≥ 51 $".
- **Expected Result**: Only restaurants with a minimum order amount of 100 $ or more are shown.
- **Status**: PASSED

## Test Case 2: Search by Restaurant Name
- **Input**: User types "Cajun" in the search bar.
- **Expected Result**: Restaurants with names containing "Cajun" appear in the results.
- **Status**: PASSED

## Test Case 3: Search by Address
- **Input**: User types "Ankara" in the search bar.
- **Expected Result**: Only restaurants located in Ankara are listed.
- **Status**: PASSED

## Test Case 4: Filter by Cuisine Type
- **Input**: User selects "Fast Food" cuisine from the filter.
- **Expected Result**: Only Fast Food restaurants are shown.
- **Status**: PASSED

## Test Case 5: No Match Found
- **Input**: User types a name/cuisine that does not exist (e.g., "Space Pizza").
- **Expected Result**: "No matching restaurants found. Check filters or search term." message is displayed.
- **Status**: PASSED