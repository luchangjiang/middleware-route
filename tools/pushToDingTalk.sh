#!/bin/bash
MSG_TITLE="主人们，$BITBUCKET_REPO_SLUG 的 $BITBUCKET_BRANCH 分支已经上线啦"
MSG_TEXT="## $MSG_TITLE\n> #### API:[$MSG_URL]($MSG_URL)\n> #### 这版本是第 $BITBUCKET_BUILD_NUMBER 次构建。来自提交 [$BITBUCKET_COMMIT](https://bitbucket.org/at-solutions/$BITBUCKET_REPO_SLUG/commits/$BITBUCKET_COMMIT)\n妾身先告退👋🏻"

linkJson='{"link":{"messageUrl":"'"$MSG_URL"'","picUrl":"","text":"'"$MSG_TEXT"'","title":"'"$MSG_TITLE"'"},"msgtype":"link"}'
textJson='{"msgtype":"text","text":{"content":"'"$MSG_TEXT"'"}}'
markdownJson='{"msgtype":"markdown","markdown":{"title":"'"$MSG_TITLE"'","text":"'"$MSG_TEXT"'"}}'
postData=$markdownJson
curl -X POST $DING_BOT_URL \
-H "Content-Type: application/json" \
-d "$postData"