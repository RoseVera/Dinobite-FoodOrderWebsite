# Use Case: Manage Menu (Categories and Foods)

## Test Case 1: Add New Category
- **Input**: Restaurant adds a new category (e.g., "Burgers").
- **Expected Result**: Category is added and appears in the menu list.
- **Status**: PASSED

## Test Case 2: Add New Food to a Category
- **Input**: Restaurant adds a food item under an existing category.
- **Expected Result**: Food item is saved and appears correctly under the category.
- **Status**: FAILED
- **Notes**: New food item only viewable after refreshing the page.

## Test Case 3: Edit Existing Category Name
- **Input**: Restaurant edits the name of a category.
- **Expected Result**: Updated name is reflected without affecting the food items under it.
- **Status**: PASSED

## Test Case 4: Edit Food Details (Name, Price, Description, Photo)
- **Input**: Restaurant updates a food item’s details.
- **Expected Result**: Updated food info is shown correctly in the menu.
- **Status**: PASSED

## Test Case 5: Attempt to Delete Category Linked to Orders
- **Input**: Restaurant tries to delete a category that contains foods.
- **Expected Result**: Deletion is blocked with a warning message "You have foods under this category, you can't delete it. Try editing it instead.".
- **Status**: PASSED

## Test Case 6: Attempt to Delete Food Linked to Orders
- **Input**: Restaurant tries to delete a food item that has existing orders.
- **Expected Result**: Deletion is blocked and user is informed with message "You have orders under this food item, you can't delete it. Try editing instead."
- **Status**: PASSED

## Test Case 7: User Successfully Removes a Category
- **Input**: Restaurant tries to delete a category that doesn't have foods under it.
- **Expected Result**: Deletion is successfull.
- **Status**: PASSED

## Test Case 8: User Successfully Removes a Food
- **Input**: Restaurant tries to delete a food that doesn't have orders under it.
- **Expected Result**: Deletion is successfull.
- **Status**: PASSED

## Test Case 9: Add New Food to a Category
- **Input**: Restaurant adds a food item under an existing category with some credentials in wrong format(e.g., photourl).
- **Expected Result**: Food is not added and system shows an error message: "The format of the values you given is incorrect."
- **Status**: PASSED