import axios from 'axios';
import type { 
  TopicTrendResponse, 
  TopicActivityResponse, 
  CoOccurrenceResponse, 
  MultithreadingResponse 
} from '../types/api';

const api = axios.create({
  baseURL: '/api',
  timeout: 10000,
});

export const topicApi = {
  getTopicList: async (): Promise<string[]> => {
    const response = await api.get<string[]>('/topics/list');
    return response.data;
  },

  getTopicTrend: async (
    topics: string[],
    startDate: string,
    endDate: string,
    period: string = 'month'
  ): Promise<TopicTrendResponse> => {
    const response = await api.get<TopicTrendResponse>('/topics/trend', {
      params: {
        topics: topics.join(','),
        startDate,
        endDate,
        period,
      },
    });
    return response.data;
  },

  getTopicActivity: async (
    topics: string[],
    startDate: string,
    endDate: string,
    period: string = 'month'
  ): Promise<TopicActivityResponse> => {
    const response = await api.get<TopicActivityResponse>('/topics/activity', {
      params: {
        topics: topics.join(','),
        startDate,
        endDate,
        period,
      },
    });
    return response.data;
  },
};

// Co-occurrence API
export const coOccurrenceApi = {
  getTopCoOccurrence: async (n: number = 10): Promise<CoOccurrenceResponse> => {
    const response = await api.get<CoOccurrenceResponse>('/occurrence/top', {
      params: { n },
    });
    return response.data;
  },
};

// Multithreading API
export const multithreadingApi = {
  getTopProblems: async (n: number = 10): Promise<MultithreadingResponse> => {
    const response = await api.get<MultithreadingResponse>('/multithreading/top', {
      params: { n },
    });
    return response.data;
  },
};

export default api;
