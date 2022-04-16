# Test Scenario Story App

## Story Repository Test

### addNewStoryWithToken()

- given imageMultiPart, descriptionMultiPart, token addNewStoryWithToken should return BaseResponse(error=false)

### getAllStoriesWithToken()

- given token getAllStoriesWithToken should not return null

## User Repository Test

### registerUser()

- given empty UserRegisterBody registerUser() should return BaseResponse(error = true)
- given UserRegisterBody with empty name registerUser() should return BaseResponse(error = true)
- given UserRegisterBody with empty email registerUser() should return BaseResponse(error = true)
- given UserRegisterBody with empty password registerUser() should return BaseResponse(error = true)
- given UserRegisterBody registerUser() should return BaseResponse(error = false)

### loginUser()

- given empty UserLoginBody loginUser() should return BaseResponse(error = true)
- given UserLoginBody with empty email loginUser() should return BaseResponse(error = true)
- given UserLoginBody with empty password loginUser() should return BaseResponse(error = true)
- given UserLoginBody loginUser() should return BaseResponse(error = false)

### clearUserToken()

- clearUserToken should be called only once when logout

## Story Repository Test

### getAllStoriesWithToken()

- given token getAllStoriesWithToken should not return null

### addNewStoryWithoutToken()

- given imageMultiPart, descriptionMultiPart, token addNewStoryWithoutToken should return
  BaseResponse(error=false)
- given exception addNewStoryWithToken should return BaseResponse(error=true)
- given BaseResponse(error = true) addNewStoryWithToken should return BaseResponse(error = true)

### getStoriesWithTokenAndLocation()

- given exception getStoriesWithTokenAndLocation should return PageResponse(error = true)

## Add Story View Model Test

- given valid argument addNewStoryToken should return BaseResponse and not null
- given image livedata image should not null when saveImage called

## Login View Model Test

- given user login body should return LoginResponse

## Map Story View Model Test

- given empty stories should return with empty data
- given stories should return with stories

## Register View Model Test

- given UserRegisterBody should BaseResponse

## Story View Model Test

- given PagingData getAllStoriesWithToken shouldn't be null

## Story Activity Test (Medium Test)

- givenSuccessResponse_shouldShowList_AndClickToDetail_AndBack()
