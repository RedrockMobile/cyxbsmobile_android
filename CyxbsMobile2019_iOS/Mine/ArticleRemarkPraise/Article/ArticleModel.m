//
//  ArticleModel.m
//  CyxbsMobile2019_iOS
//
//  Created by Stove on 2021/3/2.
//  Copyright © 2021 Redrock. All rights reserved.
//

#import "ArticleModel.h"
//删除帖子
#define CJHdeleteArticle @"https://cyxbsmobile.redrock.team/wxapi/magipoke-loop/comment/deleteId"
#define CJHgetArticle @"https://cyxbsmobile.redrock.team/wxapi/magipoke-loop/user/getUserPostList"
@implementation ArticleModel

- (void)loadMoreData {
    NSString *size = @"6";
    NSDictionary *paramDict = @{
        @"page":@(self.page),
        @"size":size,
    };
    [self.client requestWithPath:CJHgetArticle method:HttpRequestGet parameters:paramDict prepareExecute:nil progress:nil success:^(NSURLSessionDataTask *task, id responseObject) {
        [self.dataArr addObjectsFromArray:responseObject[@"data"]];
        if ([responseObject[@"data"] count]<size.integerValue) {
            [self.delegate mainPageModelLoadDataFinishWithState:StateNoMoreDate];
        }else {
            self.page++;
            [self.delegate mainPageModelLoadDataFinishWithState:StateEndRefresh];
        }
    } failure:^(NSURLSessionDataTask *task, NSError *error) {
        [self.delegate mainPageModelLoadDataFinishWithState:StateFailure];
    }];
}

- (void)deleteArticleWithID:(NSString*)ID {
    NSDictionary *paramDict = @{
        @"id":ID,
        @"model":@"0"
    };
    [self.client requestWithPath:CJHdeleteArticle method:HttpRequestPost parameters:paramDict prepareExecute:nil progress:nil success:^(NSURLSessionDataTask *task, id responseObject) {
        NSDictionary *dict;
        for (int i=0; i<self.dataArr.count; i++) {
            dict = self.dataArr[i];
            if ([dict[@"post_id"] isEqualToString:ID]) {
                [self.dataArr removeObject:dict];
                break;
            }
        }
        [self.delegate deleteArticleSuccess];
    } failure:^(NSURLSessionDataTask *task, NSError *error) {
        [self.delegate deleteArticleFailure];
    }];
}
@end
