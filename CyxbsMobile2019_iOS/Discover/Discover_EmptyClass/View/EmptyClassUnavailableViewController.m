//
//  EmptyClassUnavailableViewController.m
//  CyxbsMobile2019_iOS
//
//  Created by 钟文韬 on 2021/11/11.
//  Copyright © 2021 Redrock. All rights reserved.
//

#import "EmptyClassUnavailableViewController.h"

@interface EmptyClassUnavailableViewController ()

@end

@implementation EmptyClassUnavailableViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    [self setupBar];
    
}

- (void)setupBar{
    self.view.backgroundColor = [UIColor colorNamed:@"248_249_252_1"];
    self.VCTitleStr = @"空教室";
    self.topBarView.backgroundColor = [UIColor colorNamed:@"248_249_252_1"];
    self.splitLineColor = [UIColor colorNamed:@"BarLine"];
    self.titlePosition = TopBarViewTitlePositionLeft;
    self.titleFont = [UIFont fontWithName:PingFangSCBold size:21];
}

@end
