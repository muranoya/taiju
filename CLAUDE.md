# taiju — 体重管理アプリ

## 1. プロジェクト概要

ユーザー個人の体重管理・ダイエット支援を目的とした Android ネイティブアプリ。日々の体重を記録し、カレンダーと折れ線グラフで推移を可視化する。各日に複数のメモを残せるため、行動と体重変化の関連を後から振り返れる。データはローカル保存のみで、CSV による import/export を通じて外部とやり取りする。

## 2. 機能要件

- 日毎の体重入力 (1日1値)
- カレンダー UI で月単位の体重推移を表示
- 折れ線グラフ (X軸: 日付 / Y軸: 体重) による推移可視化
- 登録済み体重の編集・削除
- 全データの **CSV エクスポート**
- CSV からの **インポート** (UPSERT)
- 日毎メモの追加 — **件数固定ではなく自由に複数追加可能**

## 3. 技術スタック

| 領域 | 採用技術 | 備考 |
|---|---|---|
| 言語 | Kotlin | |
| UI | Jetpack Compose + Material 3 | Compose BOM で統一管理 |
| テーマ | Material You (Dynamic Color) | minSdk 31 を活かす |
| ナビゲーション | Navigation Compose | 単一 Activity |
| DB | Room | UPSERT・Flow クエリを活用 |
| アノテーション処理 | KSP | Room / Hilt 用 |
| DI | Hilt | 軽量だが将来分割を見据える |
| 非同期 | Kotlin Coroutines + Flow | |
| 日付 | kotlinx-datetime + java.time | モデルは `LocalDate`、表示は `DateTimeFormatter` |
| シリアライズ | kotlinx-serialization | 設定保存・補助用途 |
| グラフ | Vico | Compose-native の折れ線 |
| ビルド | Gradle Kotlin DSL + Version Catalog | `gradle/libs.versions.toml` で一元管理 |
| 静的解析 | ktlint | spotless 経由でも可 |

- **minSdk = 31 (Android 12)** / targetSdk は最新安定版に追従
- AGP / Kotlin / Compose Compiler は安定版の最新を使用

---

## 4. アーキテクチャ

- **単一 Activity + Compose Navigation**
- **Clean Architecture lite** (シングルモジュール `:app` から開始、複雑化したら分割)
  - `data/` — Room の entity / dao / repository 実装
  - `domain/` — プレーンなモデル / UseCase
  - `ui/` — Composable / ViewModel / Theme
  - `di/` — Hilt module
- **状態管理**: ViewModel が `StateFlow<UiState>` を公開し、Compose 側は `collectAsStateWithLifecycle` で受け取る
- **副作用** (DB I/O・CSV I/O) は UseCase に集約し、ViewModel は状態合成のみを担う

```
Composable ──(intent)──▶ ViewModel ──▶ UseCase ──▶ Repository ──▶ Room
       ◀────(StateFlow<UiState>)────┘
```

---

## 5. ディレクトリ構成

```
taiju/
├── app/
│   └── src/main/kotlin/net/meshpeak/taiju/
│       ├── data/
│       │   ├── local/         # Room: TaijuDatabase, *Entity, *Dao
│       │   └── repository/    # *Repository 実装
│       ├── domain/
│       │   ├── model/         # WeightEntry, Memo (UI/data から独立)
│       │   └── usecase/       # GetWeightHistory, UpsertWeight, ...
│       ├── ui/
│       │   ├── navigation/    # NavHost, ルート定義
│       │   ├── theme/         # Color, Typography, Theme
│       │   ├── home/          # 画面ごとに subpkg
│       │   ├── calendar/
│       │   ├── chart/
│       │   ├── detail/
│       │   ├── settings/
│       │   └── components/    # 横断的な共通 Composable
│       ├── di/                # Hilt modules
│       ├── TaijuApplication.kt
│       └── MainActivity.kt
├── gradle/
│   └── libs.versions.toml
├── build.gradle.kts
├── settings.gradle.kts
└── CLAUDE.md
```

- パッケージ名: `net.meshpeak.taiju`
- すべての Kotlin ソースは `src/main/kotlin/` 配下 (`java/` は使わない)

---

## 6. データモデル

### Room Entity

| Entity | フィールド | 制約 |
|---|---|---|
| `WeightEntry` | `id: Long` (PK, autoGenerate) / `date: LocalDate` / `weightKg: Double` / `createdAt: Instant` / `updatedAt: Instant` | `date` UNIQUE |
| `Memo` | `id: Long` (PK, autoGenerate) / `date: LocalDate` / `content: String` / `sortOrder: Int` / `createdAt: Instant` / `updatedAt: Instant` | `date` で複数件可 |

- 1日あたり: **体重は最大1件 / メモは0件以上の任意件数**
- `date` は `kotlinx.datetime.LocalDate` (Room TypeConverter で ISO-8601 文字列にシリアライズ)

---

## 7. 画面構成

| # | 画面 | 主な責務 |
|---|---|---|
| 1 | **ホーム** | 当日の体重入力 / 直近のミニグラフ / 直近メモ |
| 2 | **カレンダー** | 月表示・各日に体重バッジ・タップで日詳細へ遷移 |
| 3 | **グラフ** | 期間切替 (1週/1ヶ月/3ヶ月/全期間) の折れ線 (Vico) |
| 4 | **日詳細** | 体重編集 + メモ一覧 (追加/編集/削除/並び替え) |
| 5 | **設定** | CSV import/export・テーマ・目標体重 |

---

## 8. UI / UX 方針

- **モダンでクール** — Material 3 + Material You (Dynamic Color)。意味のあるモーション、控えめなハプティクス
- **片手操作を最優先** — 重要なタップ対象はすべて画面下部に配置する
  - 主要ナビゲーションは **Bottom Navigation Bar**
  - 記録追加は **右下 FAB**
  - 編集・選択操作は **Bottom Sheet**
  - 上部 AppBar は装飾・タイトル用に留め、操作の主導線にしない
- スワイプジェスチャを活用 (カレンダー月切替・グラフ期間切替)
- タップ領域は最低 **48dp**、隣接ターゲット間に十分な余白
- 入力フォームはキーボードで隠れないよう `imePadding` を必ず適用

---

## 9. CSV 仕様

- 文字コード: **UTF-8 (BOM 無)** / 改行: **LF** / 区切り: **`,`**
- エクスポート/インポートは **2ファイルを ZIP で一括** 扱う:

  `weights.csv`

  ```
  date,weight_kg
  2026-04-18,68.4
  ```

  `memos.csv`

  ```
  date,sort_order,content
  2026-04-18,0,"朝ランニング 5km"
  2026-04-18,1,"夜は和食"
  ```

- 値に `,` `"` 改行を含む場合は RFC 4180 準拠でクォート/エスケープ
- インポートは `date` をキーとする **UPSERT**。既存データと競合する場合は事前にダイアログで確認しインポート側を優先

---

## 10. パッケージ管理ルール

- 依存追加は **必ず `gradle/libs.versions.toml` 経由** (build.gradle.kts への直書き禁止)
- バージョンは可能な限り **メジャー寄せ** (例: `compose-bom = "2025.x.x"` のように BOM で統一)
- **`gradle.lockfile` をコミット** し、CI と開発環境のビルド再現性を担保
- 更新は明示的に `./gradlew dependencies --write-locks` を実行

---

## 11. git 運用ルール

- ユーザーから明示指示があるまで **`git add` / `git commit` を自動実行しない**
- `main` への直接コミット禁止 / `feature/<topic>` ブランチで作業
- コミットメッセージは命令形、1行目72文字以内 (日本語可)

---

## 12. テスト・検証方針

| 対象 | ツール | 配置 |
|---|---|---|
| ViewModel / UseCase | JUnit + Turbine + kotlinx-coroutines-test | `src/test/` |
| Room DAO | androidx.room testing + Robolectric/Instrumented | `src/androidTest/` |
| 主要 Composable | Compose UI Test | `src/androidTest/` |

- 型チェックとテストはコード正当性の確認であって **機能の正しさは保証しない**
- UI 変更後は **必ず Android Studio エミュレータまたは実機で起動・操作確認** を行うこと
- 確認できない環境の場合は「UI を実機で確認していない」ことを明示する (sycophantic な完了報告を避ける)

---

## 13. 開発コマンド

```bash
./gradlew assembleDebug          # debug APK ビルド
./gradlew installDebug           # エミュレータ/実機にインストール
./gradlew test                   # unit テスト (JVM)
./gradlew connectedAndroidTest   # instrumented テスト (端末必要)
./gradlew lint                   # Android Lint
./gradlew ktlintCheck            # ktlint チェック
./gradlew ktlintFormat           # ktlint 自動修正
./gradlew dependencies --write-locks   # 依存ロック更新
```
