import { useState, useEffect } from 'react';
import { topicApi } from './services/api';
import type { TopicTrendResponse, TopicActivityResponse } from './types/api';
import TopicSelector from './components/TopicSelector';
import DateRangePicker from './components/DateRangePicker';
import PeriodSelector from './components/PeriodSelector';
import TrendChart from './components/TrendChart';
import ActivityChart from './components/ActivityChart';

function App() {
  const [availableTopics, setAvailableTopics] = useState<string[]>([]);
  const [selectedTopics, setSelectedTopics] = useState<string[]>([]);
  const [startDate, setStartDate] = useState<string>('2024-01-01');
  const [endDate, setEndDate] = useState<string>('2024-12-31');
  const [period, setPeriod] = useState<string>('month');
  const [trendData, setTrendData] = useState<TopicTrendResponse | null>(null);
  const [activityData, setActivityData] = useState<TopicActivityResponse | null>(null);
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchTopics = async () => {
      try {
        const topics = await topicApi.getTopicList();
        setAvailableTopics(topics);
        if (topics.length > 0) {
          setSelectedTopics([topics[0]]);
        }
      } catch (err) {
        console.error('Error fetching topics:', err);
        setError('Failed to load topics');
      }
    };
    fetchTopics();
  }, []);

  const analyzeTopics = async () => {
    if (selectedTopics.length === 0) {
      setError('Please select at least one topic');
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const [trend, activity] = await Promise.all([
        topicApi.getTopicTrend(selectedTopics, startDate, endDate, period),
        topicApi.getTopicActivity(selectedTopics, startDate, endDate, period),
      ]);

      setTrendData(trend);
      setActivityData(activity);
    } catch (err) {
      console.error('Error analyzing topics:', err);
      setError('Analysis failed. Please check parameters and try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <header className="bg-stackoverflow-black text-white shadow-lg">
        <div className="max-w-7xl mx-auto px-4 py-6 text-center">
          <h1 className="text-3xl font-bold flex items-center justify-center gap-2">
            <span className="text-stackoverflow-orange">📊</span>
            Stack Overflow Java Topics Analysis
          </h1>
          <p className="text-gray-300 mt-2">
            Analyze trends and activity of Java-related topics
          </p>
        </div>
      </header>

      <main className="max-w-7xl mx-auto px-4 py-8">
        <div className="bg-white rounded-lg shadow-md p-6 mb-8">
          <h2 className="text-xl font-semibold mb-4 text-gray-800">
            Analysis Settings
          </h2>

          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 mb-4">
            <TopicSelector
              availableTopics={availableTopics}
              selectedTopics={selectedTopics}
              onChange={setSelectedTopics}
            />

            <DateRangePicker
              startDate={startDate}
              endDate={endDate}
              onStartDateChange={setStartDate}
              onEndDateChange={setEndDate}
            />

            <PeriodSelector period={period} onChange={setPeriod} />

            <div className="flex items-end">
              <button
                onClick={analyzeTopics}
                disabled={loading || selectedTopics.length === 0}
                className="w-full bg-stackoverflow-orange hover:bg-orange-600 text-white font-semibold py-2 px-4 rounded-lg transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
              >
                {loading ? 'Analyzing...' : 'Analyze'}
              </button>
            </div>
          </div>

          {error && (
            <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg">
              {error}
            </div>
          )}
        </div>

        {(trendData || activityData) && (
          <div className="space-y-8">
            {trendData && (
              <div className="bg-white rounded-lg shadow-md p-6">
                <h2 className="text-xl font-semibold mb-4 text-gray-800">
                  Topic Trends
                </h2>
                <p className="text-gray-600 mb-4">
                  Total Threads: <span className="font-semibold">{trendData.totalThreads}</span>
                </p>
                <TrendChart data={trendData} />
              </div>
            )}

            {activityData && (
              <div className="bg-white rounded-lg shadow-md p-6">
                <h2 className="text-xl font-semibold mb-4 text-gray-800">
                  Activity Score
                </h2>
                <p className="text-gray-600 mb-4">
                  Total Threads: <span className="font-semibold">{activityData.totalThreads}</span>
                </p>
                <ActivityChart data={activityData} />
              </div>
            )}
          </div>
        )}

        {!trendData && !activityData && !loading && (
          <div className="text-center py-12 bg-white rounded-lg shadow-md">
            <div className="text-6xl mb-4">📈</div>
            <h3 className="text-xl font-semibold text-gray-700 mb-2">
              Ready to Analyze
            </h3>
            <p className="text-gray-500">
              Select topics, date range and period, then click Analyze
            </p>
          </div>
        )}
      </main>

      <footer className="bg-gray-800 text-gray-300 mt-12 py-6">
        <div className="max-w-7xl mx-auto px-4 text-center">
          <p>Stack Overflow Java Topics Analysis</p>
        </div>
      </footer>
    </div>
  );
}

export default App;
