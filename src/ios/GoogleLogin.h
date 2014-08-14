#import <GooglePlus/GooglePlus.h>
#import <Cordova/CDV.h>

@interface GoogleLogin : CDVPlugin <GPPSignInDelegate>

@property (nonatomic, copy) NSString *callbackId;

- (void)login:(CDVInvokedUrlCommand*)command;

@end