#import "GoogleLogin.h"
#import <GoogleOpenSource/GoogleOpenSource.h>
#import <Cordova/CDV.h>

@implementation GoogleLogin

@synthesize callbackId;

- (void)login:(CDVInvokedUrlCommand*)command
{
    NSMutableDictionary* options = [command.arguments objectAtIndex:0];
    
    NSString* localCallbackId = command.callbackId;
    
    [self.commandDelegate runInBackground:^{
        CDVPluginResult* pluginResult = nil;
        NSLog(@"in plugin");
        
        if (![options objectForKey:@"clientId"]) {
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
            [self.commandDelegate sendPluginResult:pluginResult callbackId:localCallbackId];
        }
        else {
        
            GPPSignIn *signIn = [GPPSignIn sharedInstance];
            
            if (signIn.authentication) {
                NSLog(@"already authed");
                pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:signIn.authentication.accessToken];
                [self.commandDelegate sendPluginResult:pluginResult callbackId:localCallbackId];
            }
            else {
                [signIn signOut];
                NSLog(@"starting authentication");
                NSLog(@"client ID %@",[options objectForKey:@"clientId"]);
                // You previously set kClientID in the "Initialize the Google+ client" step
                signIn.clientID = [options objectForKey:@"clientId"];
    //            signIn.shouldFetchGoogleUserEmail = TRUE;
                signIn.scopes = [NSArray arrayWithObjects:
    //                             kGTLAuthScopePlusLogin, // defined in GTLPlusConstants.h
                                 @"profile",
                                 @"email",
                                 nil];
                signIn.delegate = self;
                signIn.attemptSSO = NO;
                self.callbackId = localCallbackId;
                [signIn authenticate];
            }
        }
        
        
    
    }];
}

- (void)logout:(CDVInvokedUrlCommand*)command
{
    [self.commandDelegate runInBackground:^{
        CDVPluginResult* pluginResult = nil;
        NSLog(@"in plugin");
        GPPSignIn *signIn = [GPPSignIn sharedInstance];
        [signIn signOut];
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
        
        
        
    }];
}

- (void)finishedWithAuth: (GTMOAuth2Authentication *)auth
                   error: (NSError *) error
{
    NSLog(@"Received error %@ and auth object %@",error, auth);
    CDVPluginResult* pluginResult = nil;
    if (error) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:self.callbackId];
    }
    else {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:auth.accessToken];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:self.callbackId];
    }

}


@end