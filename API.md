# 📘 API Documentation

## Base URL

```
http://localhost:8080/api
```

------

# 1. Topic Trend Analysis API

### **GET `/topics/trend`**

根据给定的 topics、时间范围与时间粒度（period），返回 StackOverflow 相关问题的趋势分析结果。

------

## 🔧 Query Parameters

| 参数名      | 类型                     | 是否必填 | 描述                                                       |
| ----------- | ------------------------ | -------- | ---------------------------------------------------------- |
| `topics`    | `string`（逗号分隔列表） | ✔ 必填   | 需要分析的话题，如：`java,spring-boot`                     |
| `startDate` | `string (YYYY-MM-DD)`    | ✔ 必填   | 起始日期（inclusive）                                      |
| `endDate`   | `string (YYYY-MM-DD)`    | ✔ 必填   | 结束日期（inclusive）                                      |
| `period`    | `string`                 | ❌ 可选   | 时间粒度：`day` / `week` / `month` / `year`。默认：`month` |

------

## 🔍 Example Request

```
GET http://localhost:8080/api/topics/trend?topics=spring-boot&startDate=2025-01-01&endDate=2025-12-31&period=week
```

------

## 📦 Response (JSON)

```
{
  "period": "week",
  "dateRange": {
    "start": "2025-01-01",
    "end": "2025-12-31"
  },
  "totalThreads": 245,
  "topicTrends": {
    "spring-boot": [
      {
        "period": "2025-W35",
        "count": 11
      },
      {
        "period": "2025-W36",
        "count": 19
      },
      {
        "period": "2025-W37",
        "count": 17
      },
      {
        "period": "2025-W38",
        "count": 16
      },
      {
        "period": "2025-W39",
        "count": 9
      },
      {
        "period": "2025-W40",
        "count": 16
      },
      {
        "period": "2025-W41",
        "count": 18
      },
      {
        "period": "2025-W42",
        "count": 20
      },
      {
        "period": "2025-W43",
        "count": 17
      },
      {
        "period": "2025-W44",
        "count": 23
      },
      {
        "period": "2025-W45",
        "count": 18
      },
      {
        "period": "2025-W46",
        "count": 15
      },
      {
        "period": "2025-W47",
        "count": 20
      },
      {
        "period": "2025-W48",
        "count": 20
      },
      {
        "period": "2025-W49",
        "count": 6
      }
    ]
  }
}
```

------

## 📘 字段说明

| 字段名                         | 描述                                                         |
| ------------------------------ | ------------------------------------------------------------ |
| `period`                       | 使用的时间粒度（day/week/month/year）。                      |
| `dateRange`                    | 用户传入的起止日期。                                         |
| `totalThreads`                 | 所有被筛选、匹配 topics 和日期范围的 thread 数量（分析数据总规模）。 |
| `topicTrends`                  | 每个 topic 对应的时间序列趋势数据。                          |
| `topicTrends[topic][i].period` | 时间片，如：`2025-W35`、`2025-01`、`2025-12-02`。            |
| `topicTrends[topic][i].count`  | 在该时间片内出现的匹配问题数量。                             |

------

# 2. Topic List API

### **GET `/topics/list`**

返回系统支持的全部 topic 名称列表。

------

##  Example Request

```
GET http://localhost:8080/api/topics/list
```

------

## 📦 Response (JSON)

```
[
  "lambda",
  "java",
  "generics",
  "reflection",
  "collections",
  "io",
  "spring-boot",
  "multithreading",
  "socket"
]
```
传入的 topics 参数必须是有效的，且在系统的 TOPIC_KEYWORDS 中定义。
------

# 🎯 Notes

- `topics` 是逗号分隔列表，如：

  ```
  topics=java,multithreading,lambda
  ```

- 时间戳整理逻辑（week、month、year）均遵循 ISO 8601 国际标准。

- 所有趋势统计基于问题的 `creationDate` 字段。

## 3. **Topic Activity Score API**

### **GET** `/api/topics/activity`

该接口用于计算并返回给定 topic 在指定时间段内的 **活动得分**，包括问题、回答和评论的权重合成。

------

## 🔧 **Query Parameters**

| 参数名      | 类型                     | 是否必填 | 描述                                                         |
| ----------- | ------------------------ | -------- | ------------------------------------------------------------ |
| `topics`    | `string`（逗号分隔列表） | ✔ 必填   | 需要分析的 topic 名单，多个 topic 用逗号分隔，如：`topics=java,spring-boot` |
| `startDate` | `string` (YYYY-MM-DD)    | ✔ 必填   | 活动分析的开始日期（包括）。                                 |
| `endDate`   | `string` (YYYY-MM-DD)    | ✔ 必填   | 活动分析的结束日期（包括）。                                 |
| `period`    | `string`                 | ❌ 可选   | 时间粒度：`day` / `week` / `month` / `year`。默认是 `month`。 |

------

## 📦 **Example Request**

```
GET http://localhost:8080/api/topics/activity?topics=spring-boot&startDate=2025-01-01&endDate=2025-12-31&period=week
```

------

## 📈 **Response (JSON)**

```
{
  "period": "week",
  "dateRange": {
    "start": "2025-01-01",
    "end": "2025-12-31"
  },
  "totalThreads": 245,
  "topicActivityScore": {
    "spring-boot": [
      {
        "period": "2025-W35",
        "activityScore": 10.2
      },
      {
        "period": "2025-W36",
        "activityScore": 24.5
      },
      {
        "period": "2025-W37",
        "activityScore": 37.8
      },
      {
        "period": "2025-W38",
        "activityScore": 34.1
      },
      {
        "period": "2025-W39",
        "activityScore": 8.6
      },
      {
        "period": "2025-W40",
        "activityScore": 20.7
      },
      {
        "period": "2025-W41",
        "activityScore": 42.6
      },
      {
        "period": "2025-W42",
        "activityScore": 36.5
      },
      {
        "period": "2025-W43",
        "activityScore": 39.2
      },
      {
        "period": "2025-W44",
        "activityScore": 62.0
      },
      {
        "period": "2025-W45",
        "activityScore": 47.5
      },
      {
        "period": "2025-W46",
        "activityScore": 28.6
      },
      {
        "period": "2025-W47",
        "activityScore": 22.3
      },
      {
        "period": "2025-W48",
        "activityScore": 37.9
      },
      {
        "period": "2025-W49",
        "activityScore": 8.2
      }
    ]
  }
}
```

------

## 📘 **字段说明**

| 字段名                                       | 描述                                                         |
| -------------------------------------------- | ------------------------------------------------------------ |
| `period`                                     | 使用的时间粒度：`day`、`week`、`month`、`year`（可选）。     |
| `dateRange`                                  | 用户传入的起始日期和结束日期。                               |
| `dateRange.start`                            | 活动分析的起始日期。                                         |
| `dateRange.end`                              | 活动分析的结束日期。                                         |
| `totalThreads`                               | 所有符合筛选条件的 StackOverflow 线程总数。                  |
| `topicActivityScore`                         | 按 topic 统计的活动分数。每个 topic 包含一个 period 数组，表示该 topic 在每个时间段内的活动分数。 |
| `topicActivityScore[topic]`                  | topic 对应的活动分数。键为 topic 名称（如 `spring-boot`），值为一个数组，包含每个时间段的 `activityScore`。 |
| `topicActivityScore[topic][i].period`        | 时间段标识，如：`2025-W35`，`2025-12`，`2025-01-01` 等。     |
| `topicActivityScore[topic][i].activityScore` | 当前时间段内该 topic 的活动分数（加权后的 score）。          |

------

## 🔑 **功能说明**

- **topics**：传入要分析的多个话题名称，每个话题对应多个关键词，用逗号分隔，如 `java,spring-boot`。
- **startDate 和 endDate**：指定活动分析的时间范围，支持 `YYYY-MM-DD` 格式。
- **period**：指定时间粒度，支持 `day`（按天），`week`（按周），`month`（按月），`year`（按年），默认按 `month` 分组。

### **活动分数计算：**

- 每个话题的活动分数是通过 **问题（question）**、**回答（answer）** 和 **评论（comment）** 的质量与创建时间来加权计算的。
- 在时间段内，`activityScore` 根据每个 `question`、`answer` 和 `comment` 的得分、创建时间以及它们的总和来计算。

------

## 📝 **备注**

1. **时间粒度：**
    默认的粒度是按月（`month`），你可以根据需要更改为按周（`week`）或按天（`day`）。
2. **活动分数的生成：**
    活动分数考虑了多个因素：
   - `question`、`answer`、`comment` 的得分。
   - 使用 **ReLU 激活函数**：负分被归零，不影响活动分数。
3. **API 限制：**
   - 请求时，确保所选的时间段（`startDate` 和 `endDate`）合理。
   - 传入的 `topics` 参数必须是有效的，且在系统的 **TOPIC_KEYWORDS** 中定义。