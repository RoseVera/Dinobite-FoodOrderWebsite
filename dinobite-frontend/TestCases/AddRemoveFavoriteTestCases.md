# Use Case: Add and Remove Favorites

## Test Case 1: Add Restaurant to Favorites
- **Input**: User clicks on the "Add to Favorites" button on a restaurant page.
- **Expected Result**: Restaurant is added to the user's favorites list.
- **Status**: PASSED

## Test Case 2: Remove Restaurant from Favorites
- **Input**: User clicks on the "Remove from Favorites" button on a restaurant already in favorites.
- **Expected Result**: Restaurant is removed from the user's favorites list.
- **Status**: PASSED

## Test Case 3: View Favorites List
- **Input**: User navigates to their profile.
- **Expected Result**: All favorite restaurants are listed correctly.
- **Status**: PASSED

## Test Case 4: Add the Same Restaurant Twice
- **Input**: User tries to favorite a restaurant that is already in their favorites.
- **Expected Result**: System does not duplicate the entry and handles gracefully.
- **Status**: PASSED

## Test Case 5: Favorites List Persists After Refresh
- **Input**: User refreshes the page or logs out and logs back in.
- **Expected Result**: Favorites list is persisted and loaded correctly.
- **Status**: PASSED
