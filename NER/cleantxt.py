file = ''
with open('./data_path/test_bio_char', 'r') as f:
    count = 0
    for l in f.readlines():
        # count += 1
        if len(l) == 1:
            continue
        file += l
        # if count == 10:
        #     break

with open('./data_path/test_bio_char', 'w') as f:
    f.write(file)