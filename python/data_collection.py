import requests
import time
import json
from API_KEY import API_KEY

API_KEY = API_KEY
BASE_URL = "https://api.stackexchange.com/2.3"
OUTPUT_FILE = "java_threads.jsonl"


def fetch(url, params):
    params["key"] = API_KEY
    params["site"] = "stackoverflow"
    r = requests.get(url, params=params)
    data = r.json()

    if "backoff" in data:
        print(f"Backoff {data['backoff']}s")
        time.sleep(data["backoff"])

    return data


# Fetch questions
def fetch_questions(num_questions=1000):
    questions = []
    page = 1

    while len(questions) < num_questions:
        print(f"Fetching questions page {page} ...")
        url = f"{BASE_URL}/questions"
        params = {
            "pagesize": 100,
            "page": page,
            "tagged": "java",
            "order": "desc",
            "sort": "creation",
            "filter": "withbody",
        }

        data = fetch(url, params)

        if "items" not in data or len(data["items"]) == 0:
            break

        questions.extend(data["items"])
        page += 1
        time.sleep(1)

    return questions[:num_questions]


# Fetch answers given question IDs
def fetch_answers(question_ids):
    ids = ";".join(str(i) for i in question_ids)
    url = f"{BASE_URL}/questions/{ids}/answers"
    params = {
        "pagesize": 100,
        "order": "desc",
        "sort": "creation",
        "filter": "withbody",
    }
    return fetch(url, params).get("items", [])


# Fetch comments on questions
def fetch_comments_for_questions(question_ids):
    ids = ";".join(str(i) for i in question_ids)
    url = f"{BASE_URL}/questions/{ids}/comments"
    params = {
        "pagesize": 100,
        "order": "desc",
        "sort": "creation",
        "filter": "withbody",
    }
    return fetch(url, params).get("items", [])


# Fetch comments on answers
def fetch_comments_on_answers(answer_ids):
    ids = ";".join(str(i) for i in answer_ids)
    url = f"{BASE_URL}/answers/{ids}/comments"
    params = {
        "pagesize": 100,
        "order": "desc",
        "sort": "creation",
        "filter": "withbody",
    }
    return fetch(url, params).get("items", [])


def main():
    questions = fetch_questions()
    print(f"Total questions fetched: {len(questions)}")

    with open(OUTPUT_FILE, "w", encoding="utf-8") as f:
        for q in questions:
            qid = q["question_id"]


            answers = fetch_answers([qid])
            answer_ids = [a["answer_id"] for a in answers]

            q_comments = fetch_comments_for_questions([qid])

            a_comments_raw = fetch_comments_on_answers(answer_ids) if answer_ids else []
            a_comments = {}
            for c in a_comments_raw:
                aid = c["post_id"]
                a_comments.setdefault(aid, []).append(c)

            # Thread structure
            thread = {
                "question": q,
                "answers": answers,
                "question_comments": q_comments,
                "answer_comments": a_comments,
            }

            f.write(json.dumps(thread) + "\n")
            time.sleep(0.3)

    print(f"Data saved to {OUTPUT_FILE}")


if __name__ == "__main__":
    main()
