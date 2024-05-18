#!/bin/bash

# 定义版本文件路径
VERSION_FILE="../app/version.properties"

# 检查文件存在
if [ ! -f "$VERSION_FILE" ]; then
    echo "Error: Version file not found at $VERSION_FILE"
    exit 1
fi

# 获取并验证当前版本号
current_version=$(grep "^version\.code=" "$VERSION_FILE" | awk -F'=' '{print $2}')
if [[ ! $current_version =~ ^[0-9]+$ ]]; then
    echo "Error: Invalid version.code format in $VERSION_FILE"
    exit 1
fi

# 更新版本号
new_version=$((current_version + 1))

# 替换文件中的版本号
sed -i "s/^version\.code=[0-9]*/version.code=$new_version/" "$VERSION_FILE"

echo "Version code updated to $new_version."

# 提示用户提交更改
echo "Please review the changes and commit manually:"
git status
