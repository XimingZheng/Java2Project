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

// Co-occurrence types
export interface CoOccurrencePair {
  topic1: string;
  topic2: string;
  count: number;
}

export interface CoOccurrenceResponse {
  totalPairs: number;
  topN: number;
  coOccurrences: CoOccurrencePair[];
}

// Multithreading pitfalls types
export interface MultithreadingProblem {
  patternName: string;
  category: string;
  count: number;
}

export interface MultithreadingResponse {
  totalThreads: number;
  topProblems: MultithreadingProblem[];
}
