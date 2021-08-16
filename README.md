

# This is backend test - Nguyen Xuan Loc


## Installation

Pull docker image

```bash
docker pull locnx1105/backend-test:version1.0
```

## Usage
Variable :
1. email_template_path (json): your email template path.
2. customer_csv_path (csv): your customer csv path.
3. output_email_directory (directory) : output email directory path. 
4. errors_csv_path (csv) : your error csv path.
## Please make sure your directory/file exist

## RUN
```sh
# run 'docker image'
docker run -v ${email_template_path}:/email_template.json -v ${customer_csv_path}:/customers.csv -v ${output_email_directory}:/Output -v ${errors_csv_path}:/errors.csv -ti locnx1105/backend-test:version1.0

# How to Enter input to console
Enter your email template : /email_template.json
Enter your customer file path : /customers.csv
Output email path : /Output
Output errors csv path : /errors.csv


```
## Result
Get {number} lines valid from customers csv 
Get {number} lines errors from customers csv 
Write Output !!
Done !!

## Final
Check {output_email_directory} and {errors_csv_path}.

## Contributing
Pull requests are welcome. This is backend test.

## License
[Loc](locnx1105@gmail.com)
