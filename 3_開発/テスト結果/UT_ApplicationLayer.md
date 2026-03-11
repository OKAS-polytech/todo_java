# 単体テスト結果報告書 (Application Layer)

## 1. テスト概要
Application Layerにおけるユースケース（入力ポート）の実装クラスである `TaskService` の動作を検証する。外部依存であるリポジトリはMockitoを用いてモック化する。

## 2. テスト環境
- Java 17
- JUnit 5.10.0
- Mockito 5.5.0

## 3. テスト実施結果

### 3.1 TaskServiceクラス (TaskServiceTest)
| ケースID | テスト項目 | 結果 | 備考 |
|:---|:---|:---|:---|
| UT-AP-01 | UC1: TODO作成が正しく実行され、リポジトリの保存メソッドが呼ばれること | OK | |
| UT-AP-02 | UC2: TODO一覧取得が正しく実行され、DTOのリストが返ること | OK | |

## 4. 判定
合格
