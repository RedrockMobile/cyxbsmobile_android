//
//  MineQAProtocol.h
//  CyxbsMobile2019_iOS
//
//  Created by 方昱恒 on 2020/2/14.
//  Copyright © 2020 Redrock. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@class MineQAMyQuestionItem, MineQAMyQuestionDraftItem;
@protocol MineQAProtocol <NSObject>

- (void)questionListRequestSucceeded:(NSArray<MineQAMyQuestionItem *> *)itemsArray;
- (void)questionDraftListRequestSucceeded:(NSArray<MineQAMyQuestionDraftItem *> *)itemsArray;

@end

NS_ASSUME_NONNULL_END
