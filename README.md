# What Is This
プロキシやプロキシ下にある全てのコマンドを操作して表示と実行を制御する強力なPlugin

# How To Use
### 導入前に
基本的にはLuckPerms等の権限管理プラグインと利用することをお勧めいたします

## 設定方法
ダウンロードしたプラグイン本体を、Velocityのプラグインフォルダに入れ、一度起動します

そうすることでデフォルトのconfigが生成されます

### Configの説明

config内の`[[permissions]]`に記述した権限を持つプレイヤーに記述した設定が反映されるようになります

LuckPermsのグループ継承等で複数の権限を持つプレイヤーの場合、すべての設定が有効化されるため、制限が多い設定が優先されます

例を言うと、`default`に`commands = ["help", "list"]`が設定されていて、`guest`がデフォルトの権限を継承するようになっている場合、たとえ`guest`が`commands = ["list"]`のように設定されていても`help`を利用することはできません

```toml
# 全てのコマンドの表示と実行を許可する権限 以下の場合`commandlimiter.admin`
allAllowed = "admin"
# 権限を何も持っていないプレイヤーのすべてのコマンドの表示と実行を制限するか
limitNoPermissionsPlayer = true

# 各 permission node で利用できるコマンドの設定
[[permissions]]
# permission node の値 以下の場合`commandlimiter.default`
nodeName = "default"
# ホワイトリストを有効にするか
# falseの場合はブラックリスト形式になる
whiteList = true
# ホワイトリストの場合は、以下に記述したコマンドのみ表示と実行が可能になる
# ブラックリストの場合は記述したコマンドのみ制限される
commands = ["help", "list", "me", "msg", "teammsg", "tell", "tm", "trigger", "w", "random"]
```

### Permission Node
このプラグインの permission node の root は `commandlimiter` になっています

LuckPerms等で設定する場合は以下のように設定する必要があります

> LuckPermsの設定をyamlで保存するようにしている場合の例
```yaml
name: default
permissions:
- commandlimiter.default:
    value: true
```

## Command
以下のコマンドが利用可能なコマンドです

| コマンド                   |エイリアス     |  説明                                          | 必要なPermissionノード |
|---------------------------|-------------|-------------------------------------------------|-----------------------|
| `/commandlimiter-reload`  | `/cl-reload` | プラグインのconfigを再読み込みして設定を反映します | `commandlimiter.admin`|

> 注意

コマンドで設定を再読み込みしたとしても、コマンドの表示に関しては**サーバーに再接続するまでクライアントでは設定前の表示**になります

これはクライアントがサーバー接続時に利用できるコマンドを取得する仕様のためです