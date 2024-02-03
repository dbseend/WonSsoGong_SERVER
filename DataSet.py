import re
import requests
import json
from bs4 import BeautifulSoup
from openai import OpenAI
import time

# 국회 공공데이터 Opan API를 통해 특정 주제에 관한 법률안 정보 습득 함수


def extract_bill_info():

    url = "https://open.assembly.go.kr/portal/openapi/TVBPMBILL11"
    params = {
        'KEY': "32f275b9db864600aa0a8d519cb29de5",
        'Type': 'json',
        'pIndex': 1,
        'pSize': 2,
        'BILL_NAME': '재난'
    }
    response = requests.get(url, params=params)
    data = response.json()

    bill_urls = [item['LINK_URL'] for item in data['TVBPMBILL11'][1]['row']]

    return bill_urls


# 법률안 정보의 URL로부터 법률안 PDF 파일 다운 함수
def download_bill(url):

    response = requests.get(url)
    soup = BeautifulSoup(response.text, 'html.parser')

    tags_a = [a for a in soup.find_all(
        'a', href=True) if a['href'].startswith('javascript:openBillFile')]

    for tag in tags_a:  # 다운을 위한 태그를 순회하며 태그 유무 확인
        href = tag['href']
        match = re.search(r"openBillFile\('(.+?)','(.+?)','(.+?)'\)", href)
        if match:
            _, id, _ = match.groups()
            break
    else:
        return "No match found"

    base_url = 'https://likms.assembly.go.kr/filegate/servlet/FileGate'
    final_url = f"{base_url}?bookId={id}&type=1"

    response = requests.get(final_url)
    if response.status_code == 200:  # 파일이 존재하는 경우
        file_name = f'{id}.pdf'
        with open(file_name, 'wb') as f:
            f.write(response.content)
        print(f"Downloaded {file_name} successfully.")
        return file_name
    else:  # 파일이 존재하지 않는 경우
        print(f"No match found for {final_url}.")
        return "No match found"

# OpenAI API Assistant를 이용해 PDF로부터 질문-답변 쌍 생성 함수


def create_assistant_and_run(file_name):

    global id

    api_key = "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"
    client = OpenAI(api_key=api_key)

    with open(file_name, "rb") as f:
        file = client.files.create(file=f, purpose="assistants")

    assistant = client.beta.assistants.create(
        name="ChatPDF",
        instructions="당신은 법률안 전문가로, 법률안에 익숙하지 않은 사람들에게 필요한 지원을 제공해야 합니다. 복잡한 법률 용어는 사람들이 쉽게 이해할 수 있도록 풀어서 설명해야 하며, 필요한 경우 그들을 대신해 법률안을 작성해야 합니다. 이때, 설명은 전문적이면서도 이해하기 쉬운 언어를 사용하고, 법률안 작성은 사용자의 요구와 상황에 맞게 맞춤화해야 합니다.",
        tools=[{"type": "retrieval"}],
        # model="gpt-3.5-turbo-1106",
        model="gpt-4-1106-preview",
        file_ids=[file.id]
    )

    thread = client.beta.threads.create()

    summarizeQuestion = "법률안을 분석하여, 주요 절차, 관련 법조, 주요 이슈 등을 포함한 각 목차별 핵심 내용을 리스트 형식으로 정리해 보여줘."
    client.beta.threads.messages.create(
        thread_id=thread.id,
        role="user",
        content=summarizeQuestion
    )

    run = client.beta.threads.runs.create(
        thread_id=thread.id,
        assistant_id=assistant.id
    )

    while run.status not in ['completed', 'failed']:  # 텍스트 생성 진행 상황 알림
        run = client.beta.threads.runs.retrieve(
            thread_id=thread.id,
            run_id=run.id
        )

    if run.status == 'failed':  # 텍스트 생성 실패시 출력문과 함께 리턴
        print("The assistant run has failed.")
        return

    qaListQuestion = '각 법률안 목차에 대한 분석 결과를 기반으로, 질문과 그에 대한 명확한 답변으로 이루어진 쌍을 다섯 개씩 작성해줘. 질문은 목차 이해를 도울 것이고, 답변은 구체적인 정보를 제공할 것이다. 아래 형식을 따라 작성해줘.\n질문: \n답변: \n'
    client.beta.threads.messages.create(
        thread_id=thread.id,
        role="user",
        content=qaListQuestion
    )

    run = client.beta.threads.runs.create(
        thread_id=thread.id,
        assistant_id=assistant.id
    )

    while run.status not in ['completed', 'failed']:  # 텍스트 생성 진행 상황 알림
        run = client.beta.threads.runs.retrieve(
            thread_id=thread.id,
            run_id=run.id
        )

    if run.status == 'failed':  # 텍스트 생성 실패시 출력문과 함께 리턴
        print("The assistant run has failed.")
        return

    qaResponse = client.beta.threads.messages.list(
        thread_id=thread.id
    )

    qaContents = qaResponse.data[0].content[0].text.value
    # Split the data into individual Q&A pairs
    pairs = qaContents.strip().split("\n\n")

    # Initialize an empty list to hold the processed pairs
    qaList = []
    # Process each pair
    for i, pair in enumerate(pairs):
        question = pair.split(': ')[1].split('\n')[0]
        answer = pair.split(': ')[2].replace('\n', ' ').strip()

        qaList.append({
            "id": id,
            "question": question,
            "answers": {"text": answer}
        })
        id += 1

    print(qaList)
    Bill_DataSet.append(qaList)

# 생성한 질문-답변 쌍들을 json 파일 형식으로 저장시키는 함수
def save_to_json(data, filename):
    with open(filename, 'w', encoding='utf-8') as f:
        json.dump(data, f, ensure_ascii=False, indent=4)


id = 1
Bill_DataSet = []

bill_urls = extract_bill_info()
for bill_url in bill_urls:
    file_name = download_bill(bill_url)

    if file_name != "No match found":
        create_assistant_and_run(file_name)
        save_to_json(Bill_DataSet, 'CalamityBill4.json')
    else:
        pass
        print(f"Skipping {bill_url} due to no match found.")

    time.sleep(20)
