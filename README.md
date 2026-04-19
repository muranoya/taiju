# taiju

日々の体重とメモを記録し、カレンダーとグラフで推移を振り返るための Android ネイティブアプリ。

## 概要

ユーザー個人の体重管理・ダイエット支援を目的とした Android アプリ。1 日 1 値の体重を入力し、カレンダーと折れ線グラフで推移を可視化する。各日に複数のメモを残せるため、行動と体重変化の関連を後から振り返れる。データは端末ローカルにのみ保存し、外部とのやりとりは CSV (ZIP) の import / export を経由する。

## 主な機能

- 日毎の体重入力（1 日 1 値、ホイールピッカー）
- ホーム画面の直近 7 日ミニグラフ
- グラフ画面の期間切替（1 週 / 1 ヶ月 / 3 ヶ月 / 全期間）折れ線
- カレンダーでの月表示と日詳細遷移
- 日毎メモ（件数固定なし・自由に追加 / 編集 / 削除 / 並び替え）
- CSV (ZIP) でのインポート / エクスポート

## 技術スタック

| 領域 | 採用技術 |
|---|---|
| 言語 | Kotlin 2.1.0 |
| UI | Jetpack Compose (BOM 2024.12.01) + Material 3 |
| ビルド | AGP 8.7.3 + Gradle Kotlin DSL + Version Catalog |
| DB | Room 2.6.1 (KSP) |
| DI | Hilt 2.53 |
| 非同期 | Kotlinx Coroutines 1.9.0 / Flow |
| 日付 | kotlinx-datetime 0.6.1 |
| シリアライズ | kotlinx-serialization 1.7.3 |
| グラフ | Vico 2.0.0-beta.3 |
| 設定保存 | Jetpack DataStore 1.1.1 |
| 静的解析 | ktlint (jlleitschuh plugin 12.1.1) |

| ビルド設定 | 値 |
|---|---|
| `applicationId` | `net.meshpeak.taiju`（debug は `.debug` サフィックス） |
| `minSdk` | 31 (Android 12) |
| `compileSdk` / `targetSdk` | 35 (Android 15) |
| `versionCode` / `versionName` | `1` / `0.1.0` |
| Java / Kotlin target | 17 |

## 必要要件

- JDK 17
- Android SDK（`ANDROID_HOME` を設定し、Platform 35 以上をインストール）
- [`just`](https://github.com/casey/just)

## 開発: just コマンド

`justfile` でよく使うタスクをラップしてある。引数なしで `just` を実行するとレシピ一覧が表示される。

| コマンド | 内容 |
|---|---|
| `just` | レシピ一覧を表示（デフォルト） |
| `just check` | `./gradlew ktlintCheck lint` を実行 |
| `just format` | `./gradlew ktlintFormat` で Kotlin を自動整形 |
| `just run` | Debug APK をビルド → インストール → 起動。デバイス未接続なら AVD を自動起動してブート完了まで待機 |
| `just release` | Unsigned Release APK をビルドし、出力パスと署名が必要な旨を表示 |
| `just clean` | `./gradlew clean` |

## リリース手順

リリースは GitHub Actions の [`Release` ワークフロー](./.github/workflows/release.yml) を手動起動（`workflow_dispatch`）して行う。

### 1. バージョンを上げる

`VERSION` ファイルを semver (`MAJOR.MINOR.PATCH`) で更新し、commit & push する。各要素は 0..99 の範囲でなければならない。

```sh
echo "0.2.0" > VERSION
git add VERSION
git commit -m "bump version to 0.2.0"
git push
```

`app/build.gradle.kts` が `VERSION` を読み取り、`versionName` にそのまま、`versionCode` は `major*10000 + minor*100 + patch` に変換して設定する（例: `0.2.0` → `versionCode = 200`）。

### 2. Release ワークフローを起動

GitHub の Actions タブ → `Release` → **Run workflow** を押す。`dry_run` の扱いは次のとおり。

- `dry_run: true` — APK をビルドして workflow アーティファクトとしてアップロードするのみ。タグや GitHub Release は作成しない。署名や keystore 周りの検証に使う。
- `dry_run: false`（既定）— 以下を自動で行う。
  1. `just check` を実行
  2. タグ `v<VERSION>` が存在しないことを確認（存在したら失敗）
  3. 署名用シークレット 4 種が揃っていることを検証
  4. `just build-release` で署名済み APK を生成
  5. `taiju-v<VERSION>.apk` にリネーム
  6. GitHub Release（自動生成リリースノート付き）を作成し、APK を添付

同じバージョンで 2 回目のリリースはできない。再リリースしたい場合は `VERSION` をインクリメントする。

### 3. リリースに必要な GitHub Secrets

次の 4 つを必ずリポジトリの **Settings → Secrets and variables → Actions** に登録しておく。1 つでも欠けるとワークフローが `Verify signing secrets are present` ステップで失敗する。

| Secret 名 | 内容 |
| --- | --- |
| `RELEASE_KEYSTORE_BASE64` | 署名用 keystore (`.jks`) を `base64` エンコードした文字列 |
| `RELEASE_KEYSTORE_PASSWORD` | keystore のパスワード |
| `RELEASE_KEY_ALIAS` | 署名に使うキーのエイリアス |
| `RELEASE_KEY_PASSWORD` | エイリアスのパスワード |

keystore を base64 化する例:

```sh
base64 -w0 release.jks   # Linux
base64 -i release.jks    # macOS
```

### 4. ローカルで署名済み APK をビルドする（任意）

CI に出す前に手元で確認したい場合は、次の 4 つの環境変数を設定してから `just build-release` を実行する。

```sh
export RELEASE_KEYSTORE_PATH=/absolute/path/to/release.jks
export RELEASE_KEYSTORE_PASSWORD=...
export RELEASE_KEY_ALIAS=...
export RELEASE_KEY_PASSWORD=...
just build-release
```

生成物は `app/build/outputs/apk/release/app-release.apk`。環境変数が未設定の場合、`app/build.gradle.kts` の `signingConfigs` ブロックが条件分岐して **未署名ビルド**になる（インストール不可）。

## 詳細仕様

設計方針・データモデル・画面構成・CSV フォーマット・UI/UX 原則の詳細は [`CLAUDE.md`](./CLAUDE.md) を参照。
