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

// Solvable Analysis types
export interface AnalysisFactor {
  solvableAvg?: number;
  notSolvableAvg?: number;
  solvableAvgCharacters?: number;
  notSolvableAvgCharacters?: number;
  solvableAvgWords?: number;
  notSolvableAvgWords?: number;
  solvableWithCodePercentage?: number;
  notSolvableWithCodePercentage?: number;
  solvableAvgCodeBlocks?: number;
  notSolvableAvgCodeBlocks?: number;
  solvableAvgTags?: number;
  notSolvableAvgTags?: number;
  solvableAvgResponseHours?: number;
  notSolvableAvgResponseHours?: number;
  notSolvableNoAnswerPercentage?: number;
  solvableAvgScore?: number;
  notSolvableAvgScore?: number;
  solvableAvgViews?: number;
  notSolvableAvgViews?: number;
  difference?: number;
  percentageDifference?: number;
  characterDifference?: number;
  wordDifference?: number;
}

export interface BasicStats {
  totalSolvable: number;
  totalNotSolvable: number;
  totalQuestions: number;
  solvablePercentage: number;
  notSolvablePercentage: number;
}

export interface SolvableAnalysisResponse {
  reputationAnalysis: AnalysisFactor;
  questionLengthAnalysis: AnalysisFactor;
  codeSnippetAnalysis: AnalysisFactor;
  tagCountAnalysis: AnalysisFactor;
  responseTimeAnalysis: AnalysisFactor;
  questionScoreAnalysis: AnalysisFactor;
  viewCountAnalysis: AnalysisFactor;
  titleLengthAnalysis: AnalysisFactor;
  basicStats: BasicStats;
}
