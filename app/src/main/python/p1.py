from gtts import gTTS
import os
def convert_text_to_speech(text, output_file_path):
    tts = gTTS(text=text, lang='en')
    tts.save(output_file_path)

def main(txi,txo):
    input_file = txi
    output_file = txo

    if not os.path.isfile(input_file):
        print(f'Error: The input file "{input_file}" does not exist.')
        return

    with open(input_file, 'r', encoding='utf-8') as f:
        research_paper_text = f.read()

    convert_text_to_speech(research_paper_text, output_file)


