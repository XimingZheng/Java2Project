import React from 'react';

interface TopicSelectorProps {
  availableTopics: string[];
  selectedTopics: string[];
  onChange: (topics: string[]) => void;
}

const TopicSelector: React.FC<TopicSelectorProps> = ({
  availableTopics,
  selectedTopics,
  onChange,
}) => {
  const handleToggleTopic = (topic: string) => {
    if (selectedTopics.includes(topic)) {
      onChange(selectedTopics.filter((t) => t !== topic));
    } else {
      onChange([...selectedTopics, topic]);
    }
  };

  return (
    <div className="col-span-1">
      <label className="block text-sm font-medium text-gray-700 mb-2">
        Topics
      </label>
      <div className="relative">
        <details className="group">
          <summary className="list-none cursor-pointer border border-gray-300 rounded-lg px-4 py-2 bg-white hover:bg-gray-50">
            <div className="flex justify-between items-center">
              <span className="text-sm">
                {selectedTopics.length > 0
                  ? `${selectedTopics.length} selected`
                  : 'Select topics'}
              </span>
              <svg
                className="w-4 h-4 transition-transform group-open:rotate-180"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M19 9l-7 7-7-7"
                />
              </svg>
            </div>
          </summary>
          <div className="absolute z-10 mt-2 w-full bg-white border border-gray-300 rounded-lg shadow-lg max-h-60 overflow-y-auto">
            {availableTopics.map((topic) => (
              <label
                key={topic}
                className="flex items-center px-4 py-2 hover:bg-gray-50 cursor-pointer"
              >
                <input
                  type="checkbox"
                  checked={selectedTopics.includes(topic)}
                  onChange={() => handleToggleTopic(topic)}
                  className="mr-2 h-4 w-4 text-stackoverflow-orange focus:ring-stackoverflow-orange border-gray-300 rounded"
                />
                <span className="text-sm">{topic}</span>
              </label>
            ))}
          </div>
        </details>
      </div>
      {selectedTopics.length > 0 && (
        <div className="mt-2 flex flex-wrap gap-2">
          {selectedTopics.map((topic) => (
            <span
              key={topic}
              className="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium bg-stackoverflow-orange text-white"
            >
              {topic}
              <button
                onClick={() => handleToggleTopic(topic)}
                className="ml-1 hover:text-gray-200"
              >
                ×
              </button>
            </span>
          ))}
        </div>
      )}
    </div>
  );
};

export default TopicSelector;
