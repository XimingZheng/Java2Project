import { useState, useEffect } from 'react';
import { topicApi, coOccurrenceApi, multithreadingApi } from './services/api';
import type { 
  TopicTrendResponse, 
  TopicActivityResponse, 
  CoOccurrenceResponse, 
  MultithreadingResponse 
} from './types/api';
import TopicSelector from './components/TopicSelector';
import DateRangePicker from './components/DateRangePicker';
import PeriodSelector from './components/PeriodSelector';
import TrendChart from './components/TrendChart';
import ActivityChart from './components/ActivityChart';
import CoOccurrenceChart from './components/CoOccurrenceChart';
import MultithreadingChart from './components/MultithreadingChart';

function App() {
  // Topic analysis states
  const [availableTopics, setAvailableTopics] = useState<string[]>([]);
  const [selectedTopics, setSelectedTopics] = useState<string[]>([]);
  const [startDate, setStartDate] = useState<string>('2024-01-01');
  const [endDate, setEndDate] = useState<string>('2024-12-31');
  const [period, setPeriod] = useState<string>('month');
  const [trendData, setTrendData] = useState<TopicTrendResponse | null>(null);
  const [activityData, setActivityData] = useState<TopicActivityResponse | null>(null);
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);

  // Co-occurrence states
  const [coOccurrenceData, setCoOccurrenceData] = useState<CoOccurrenceResponse | null>(null);
  const [coOccurrenceN, setCoOccurrenceN] = useState<number>(10);
  const [coOccurrenceLoading, setCoOccurrenceLoading] = useState<boolean>(false);

  // Multithreading states
  const [multithreadingData, setMultithreadingData] = useState<MultithreadingResponse | null>(null);
  const [multithreadingN, setMultithreadingN] = useState<number>(10);
  const [multithreadingLoading, setMultithreadingLoading] = useState<boolean>(false);

  // Active tab state
  const [activeTab, setActiveTab] = useState<'trends' | 'cooccurrence' | 'multithreading'>('trends');

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

  // Analyze topics (trends + activity)
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

  // Analyze co-occurrence
  const analyzeCoOccurrence = async () => {
    setCoOccurrenceLoading(true);
    try {
      const data = await coOccurrenceApi.getTopCoOccurrence(coOccurrenceN);
      setCoOccurrenceData(data);
    } catch (err) {
      console.error('Error analyzing co-occurrence:', err);
      setError('Failed to fetch co-occurrence data');
    } finally {
      setCoOccurrenceLoading(false);
    }
  };

  // Analyze multithreading pitfalls
  const analyzeMultithreading = async () => {
    setMultithreadingLoading(true);
    try {
      const data = await multithreadingApi.getTopProblems(multithreadingN);
      setMultithreadingData(data);
    } catch (err) {
      console.error('Error analyzing multithreading:', err);
      setError('Failed to fetch multithreading data');
    } finally {
      setMultithreadingLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <header className="bg-stackoverflow-black text-white shadow-lg">
        <div className="max-w-7xl mx-auto px-4 py-6 text-center">
          <h1 className="text-3xl font-bold flex items-center justify-center gap-2">
            <span className="text-stackoverflow-orange"></span>
            Stack Overflow Analysis
          </h1>
          <p className="text-gray-300 mt-2">
            Analysis of Java programming topics on Stack Overflow
          </p>
        </div>
      </header>

      {/* Tab Navigation */}
      <nav className="bg-white shadow-sm border-b border-gray-200">
        <div className="max-w-7xl mx-auto px-4">
          <div className="flex gap-1">
            <button
              onClick={() => setActiveTab('trends')}
              className={`px-6 py-3 font-medium text-sm transition-colors ${
                activeTab === 'trends'
                  ? 'border-b-2 border-stackoverflow-orange text-stackoverflow-orange'
                  : 'text-gray-600 hover:text-gray-800'
              }`}
            >
              📈 Topic Trends
            </button>
            <button
              onClick={() => setActiveTab('cooccurrence')}
              className={`px-6 py-3 font-medium text-sm transition-colors ${
                activeTab === 'cooccurrence'
                  ? 'border-b-2 border-stackoverflow-orange text-stackoverflow-orange'
                  : 'text-gray-600 hover:text-gray-800'
              }`}
            >
              🔗 Topic Co-occurrence
            </button>
            <button
              onClick={() => setActiveTab('multithreading')}
              className={`px-6 py-3 font-medium text-sm transition-colors ${
                activeTab === 'multithreading'
                  ? 'border-b-2 border-stackoverflow-orange text-stackoverflow-orange'
                  : 'text-gray-600 hover:text-gray-800'
              }`}
            >
              ⚠️ Multithreading Pitfalls
            </button>
          </div>
        </div>
      </nav>

      <main className="max-w-7xl mx-auto px-4 py-8">
        {/* Topic Trends Tab */}
        {activeTab === 'trends' && (
          <>
            <div className="bg-white rounded-lg shadow-md p-6 mb-8">
              <h2 className="text-xl font-semibold mb-4 text-gray-800">
                Topic Trends Analysis Settings
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
                      Topic Trends Over Time
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
                      Topic Activity Score
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
          </>
        )}

        {/* Co-occurrence Tab */}
        {activeTab === 'cooccurrence' && (
          <>
            <div className="bg-white rounded-lg shadow-md p-6 mb-8">
              <h2 className="text-xl font-semibold mb-4 text-gray-800">
                Topic Co-occurrence Analysis
              </h2>
              <p className="text-gray-600 mb-4">
                Discover which Java topics frequently appear together in Stack Overflow threads
              </p>

              <div className="flex gap-4 items-end">
                <div className="flex-1">
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Number of Top Pairs
                  </label>
                  <input
                    type="number"
                    min="5"
                    max="20"
                    value={coOccurrenceN}
                    onChange={(e) => setCoOccurrenceN(Number(e.target.value))}
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-stackoverflow-orange focus:border-transparent"
                  />
                </div>
                <button
                  onClick={analyzeCoOccurrence}
                  disabled={coOccurrenceLoading}
                  className="px-8 py-2 bg-stackoverflow-orange hover:bg-orange-600 text-white font-semibold rounded-lg transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  {coOccurrenceLoading ? 'Loading...' : 'Analyze'}
                </button>
              </div>
            </div>

            {coOccurrenceData && (
              <div className="bg-white rounded-lg shadow-md p-6">
                <h2 className="text-xl font-semibold mb-4 text-gray-800">
                  Top Topic Co-occurrences
                </h2>
                <CoOccurrenceChart data={coOccurrenceData} />
              </div>
            )}

            {!coOccurrenceData && !coOccurrenceLoading && (
              <div className="text-center py-12 bg-white rounded-lg shadow-md">
                <div className="text-6xl mb-4">🔗</div>
                <h3 className="text-xl font-semibold text-gray-700 mb-2">
                  Ready to Analyze
                </h3>
                <p className="text-gray-500">
                  Click Analyze to discover topic co-occurrence patterns
                </p>
              </div>
            )}
          </>
        )}

        {/* Multithreading Pitfalls Tab */}
        {activeTab === 'multithreading' && (
          <>
            <div className="bg-white rounded-lg shadow-md p-6 mb-8">
              <h2 className="text-xl font-semibold mb-4 text-gray-800">
                Multithreading Common Pitfalls Analysis
              </h2>
              <p className="text-gray-600 mb-4">
                Identify the most common problems and challenges developers face with Java multithreading
              </p>

              <div className="flex gap-4 items-end">
                <div className="flex-1">
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Number of Top Problems
                  </label>
                  <input
                    type="number"
                    min="5"
                    max="20"
                    value={multithreadingN}
                    onChange={(e) => setMultithreadingN(Number(e.target.value))}
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-stackoverflow-orange focus:border-transparent"
                  />
                </div>
                <button
                  onClick={analyzeMultithreading}
                  disabled={multithreadingLoading}
                  className="px-8 py-2 bg-stackoverflow-orange hover:bg-orange-600 text-white font-semibold rounded-lg transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  {multithreadingLoading ? 'Loading...' : 'Analyze'}
                </button>
              </div>
            </div>

            {multithreadingData && (
              <div className="bg-white rounded-lg shadow-md p-6">
                <h2 className="text-xl font-semibold mb-4 text-gray-800">
                  Top Multithreading Pitfalls
                </h2>
                <MultithreadingChart data={multithreadingData} />
              </div>
            )}

            {!multithreadingData && !multithreadingLoading && (
              <div className="text-center py-12 bg-white rounded-lg shadow-md">
                <div className="text-6xl mb-4">⚠️</div>
                <h3 className="text-xl font-semibold text-gray-700 mb-2">
                  Ready to Analyze
                </h3>
                <p className="text-gray-500">
                  Click Analyze to discover common multithreading pitfalls
                </p>
              </div>
            )}
          </>
        )}
      </main>

      <footer className="bg-gray-800 text-gray-300 mt-12 py-6">
        <div className="max-w-7xl mx-auto px-4 text-center">
          <p className="text-sm">
            Stack Overflow Java Topics Analysis Dashboard · CS209A Final Project
          </p>
        </div>
      </footer>
    </div>
  );
}

export default App;
