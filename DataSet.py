import re
import requests
import json
from bs4 import BeautifulSoup
from openai import OpenAI
import time

#국회 공공데이터 Opan API를 통해 특정 주제에 관한 법률안 정보 습득 함수
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


#법률안 정보의 URL로부터 법률안 PDF 파일 다운 함수
def download_bill(url):

    response = requests.get(url)
    soup = BeautifulSoup(response.text, 'html.parser')

    tags_a = [a for a in soup.find_all(
        'a', href=True) if a['href'].startswith('javascript:openBillFile')]

    for tag in tags_a: #다운을 위한 태그를 순회하며 태그 유무 확인
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
    if response.status_code == 200: # 파일이 존재하는 경우
        file_name = f'{id}.pdf'
        with open(file_name, 'wb') as f:
            f.write(response.content)
        print(f"Downloaded {file_name} successfully.")
        return file_name
    else: # 파일이 존재하지 않는 경우
        print(f"No match found for {final_url}.")
        return "No match found"

#OpenAI API Assistant를 이용해 PDF로부터 질문-답변 쌍 생성 함수
def create_assistant_and_run(file_name):

    global id

    question = "위 보고서 내용과 법 정보에 관해, 아래 포맷에 맞춘 질문-답변 쌍을 한국어로 10개 생성해주세요.\n1. 질문: \n   답변: \n2. 질문: \n   답변: \n3. 질문: \n   답변: \n4. 질문: \n   답변: \n5. 질문: \n   답변: \n6. 질문: \n   답변: \n7. 질문: \n   답변: \n8. 질문: \n   답변: \n9. 질문: \n   답변: \n10. 질문: \n   답변: "
    api_key = "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"

    client = OpenAI(api_key=api_key)

    with open(file_name, "rb") as f:
        file = client.files.create(file=f, purpose="assistants")

    assistant = client.beta.assistants.create(
        name="ChatPDF",
        instructions="너는 법률안 분석 및 작성 전문가야.",
        tools=[{"type": "retrieval"}],
        # model="gpt-3.5-turbo-1106",
        model="gpt-4-1106-preview",
        file_ids=[file.id]
    )

    thread = client.beta.threads.create()

    client.beta.threads.messages.create(
        thread_id=thread.id,
        role="user",
        content=question
    )

    run = client.beta.threads.runs.create(
        thread_id=thread.id,
        assistant_id=assistant.id
    )

    while run.status not in ['completed', 'failed']: #텍스트 생성 진행 상황 알림
        run = client.beta.threads.runs.retrieve(
            thread_id=thread.id,
            run_id=run.id
        )

    if run.status == 'failed': #텍스트 생성 실패시 출력문과 함께 리턴
        print("The assistant run has failed.")
        return

    messages = client.beta.threads.messages.list(
        thread_id=thread.id
    )

    sentences = []
    qaListLenght = len(sentences)
    if(qaListLenght % 2 == 1): #qa리스트 문장 개수가 홀수 확인(제대로 된 문장 생성 여부 확인)
        sentences.pop(0)

    for message in messages.data: #습득한 텍스트로 부터 질문-답 리스트로 변환
        if message.role == 'assistant': #Assisant가 생성한 답변으로 리스트 생성
            sentences = message.content[0].text.value.split('\n')
            sentences = list(filter(None, (sentence.strip()
                             for sentence in sentences)))
            sentences = [s for s in sentences if "질문-답변 쌍" not in s]

    modified_sentences = []
    for index, sentence in enumerate(sentences): #질문-답 리스트 텍스트 전처리
        modified_sentence = re.sub(r"【.*?】", "", sentence)  # 【...】 부분 제거
        start_index = 6 if index % 2 == 0 else 3  # 순서에 따라 시작 인덱스 선택
        modified_sentence = modified_sentence[start_index:]  # 시작 인덱스 이후부터 보여주기

        if modified_sentence and modified_sentence[0] == " ": #첫글자 공백체크 및 공백 제거
            modified_sentence = modified_sentence[1:]
        modified_sentences.append(modified_sentence)

    answer_question_pairs = []
    for idx in range(0, len(modified_sentences), 2):  #질문-답변 쌍을 하나의 리스트에 저장
        question = modified_sentences[idx]
        answer = modified_sentences[idx + 1] if idx + 1 < len(modified_sentences) else ''
        answer_question_pairs.append({
            'id': id,
            'question': question,
            'answers': {'text': answer}
        })
        id += 1


    # print(len(answer_question_pairs))
    if(len(answer_question_pairs) != 1):
        Bill_DataSet.append({
            'qas': [answer_question_pairs]
        })
    else:
        print("데이터가 부족합니다")

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
        save_to_json(Bill_DataSet, 'CalamityBill3.json')
    else:
        pass
        print(f"Skipping {bill_url} due to no match found.")

    time.sleep(20)

