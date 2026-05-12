# Use Case: Rate and Review Restaurants

## Test Case 1: Leave Rating Without Review
- **Input**: User selects 4 stars for a restaurant they ordered from, without typing a review.
- **Expected Result**: Rating is saved successfully and visible in restaurant profile.
- **Status**: PASSED

## Test Case 2: Leave Review with Rating
- **Input**: User selects 5 stars and writes a positive review (e.g., "Great food and fast delivery").
- **Expected Result**: Rating and review are saved and displayed correctly.
- **Status**: PASSED

## Test Case 3: Attempt to Rate Without Previous Order
- **Input**: User tries to rate a restaurant they haven’t ordered from.
- **Expected Result**: User cant review a non-existed order.
- **Status**: PASSED

## Test Case 4: Edit Existing Review
- **Input**: User modifies their previous review text and changes rating from 3 to 4 stars.
- **Expected Result**: Updated review and rating are saved and shown properly.
- **Status**: PASSED

## Test Case 5: Delete Review
- **Input**: User deletes their existing review.
- **Expected Result**: Review and rating are removed from the restaurant page.
- **Status**: PASSED

## Test Case 6: User Responds To a Respond From Restaurant
- **Input**: User sees restaurants respond. Writes a new comment under it.
- **Expected Result**: comment created successfully and showed in restaurant page.
- **Status**: PASSED

## Test Case 7: User Wants to write a review for an order Second Time
- **Input**: User tries to write a second comment.
- **Expected Result**: system doesn't show the create comment button and incase shows a warning saying "You cannot comment a second time.".
- **Status**: PASSED