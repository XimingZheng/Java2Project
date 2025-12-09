export interface TrendDataPoint {
  period: string;
  count: number;
}

export interface ActivityDataPoint {
  period: string;
  activityScore: number;
}

export interface DateRange {
  start: string;
  end: string;
}

export interface TopicTrendResponse {
  period: string;
  dateRange: DateRange;
  totalThreads: number;
  topicTrends: {
    [topic: string]: TrendDataPoint[];
  };
}

export interface TopicActivityResponse {
  period: string;
  dateRange: DateRange;
  totalThreads: number;
  topicActivityScore: {
    [topic: string]: ActivityDataPoint[];
  };
}
