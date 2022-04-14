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
