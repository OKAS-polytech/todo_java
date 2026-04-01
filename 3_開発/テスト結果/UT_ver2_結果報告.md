# 単体テスト結果報告書 (ver2: ユーザー・グループ管理)

## 1. テスト概要
ver2で追加されたユーザー管理、グループ管理、およびタスク共有機能の正当性を検証する。

## 2. テスト環境
- Java 17
- JUnit 5.10.0
- Mockito 5.5.0

## 3. テスト実施結果

### 3.1 AuthService (AuthServiceTest)
| ケースID | テスト項目 | 結果 | 備考 |
|:---|:---|:---|:---|
| UT-AU-01 | 正常系：ユーザー登録ができること | OK | |
| UT-AU-02 | 正常系：ログインができること | OK | |

### 3.2 TaskService (TaskServiceTest, TaskServiceFullTest)
| ケースID | テスト項目 | 結果 | 備考 |
|:---|:---|:---|:---|
| UT-AP-01 | 正常系：ユーザー・グループID付きのタスク作成 | OK | |
| UT-AP-02 | 正常系：特定ユーザーのタスク一覧取得 | OK | |
| UT-AP-03 | 正常系：タスク更新ロジック | OK | |
| UT-AP-04 | 正常系：ユーザーID指定の一括削除 | OK | |

### 3.3 Persistence (SQLiteUserRepositoryTest, SQLiteGroupRepositoryTest)
- ※ 代表的なリポジトリテストは結合テストにて網羅。

## 4. 判定
合格
