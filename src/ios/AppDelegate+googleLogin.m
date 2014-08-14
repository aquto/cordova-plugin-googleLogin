//
//  AppDelegate+googleLogin.m
//

#import "AppDelegate+googleLogin.h"
#import <GooglePlus/GooglePlus.h>

@implementation AppDelegate (googleLogin)

- (BOOL)application: (UIApplication *)application
            openURL: (NSURL *)url
  sourceApplication: (NSString *)sourceApplication
         annotation: (id)annotation {
    if (!url) {
        return NO;
    }
    
    [GPPURLHandler handleURL:url
                  sourceApplication:sourceApplication
                         annotation:annotation];

    
    // calls into javascript global function 'handleOpenURL'
    NSString* jsString = [NSString stringWithFormat:@"handleOpenURL(\"%@\");", url];
    [self.viewController.webView stringByEvaluatingJavaScriptFromString:jsString];
    
    // all plugins will get the notification, and their handlers will be called
    [[NSNotificationCenter defaultCenter] postNotification:[NSNotification notificationWithName:CDVPluginHandleOpenURLNotification object:url]];
    
    return YES;
}


@end
